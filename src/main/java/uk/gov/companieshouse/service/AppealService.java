package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.ReasonType;
import uk.gov.companieshouse.repository.AppealRepository;

@Service
public class AppealService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AppealMapper appealMapper;
    private final AppealRepository appealRepository;
    private final ChipsRestClient chipsRestClient;
    private final ChipsConfiguration chipsConfiguration;

    @Autowired
    public AppealService(AppealMapper appealMapper, AppealRepository appealRepository, ChipsRestClient chipsRestClient, ChipsConfiguration chipsConfiguration) {
        this.appealMapper = appealMapper;
        this.appealRepository = appealRepository;
        this.chipsRestClient = chipsRestClient;
        this.chipsConfiguration = chipsConfiguration;
    }

    public String saveAppeal(Appeal appeal, String userId) {
        appeal.setCreatedAt(LocalDateTime.now());
        appeal.getCreatedBy().setId(userId);

        String appealId = createAppealInMongoDB(appeal, userId);
        appeal.setId(appealId);

        if (chipsConfiguration.isChipsEnabled()) {
            createContactInChips(appeal, userId);
        } else {
            LOGGER.debug("CHIPS feature is disabled");
        }

        return appealId;
    }

    private String createAppealInMongoDB(Appeal appeal, String userId) {
        LOGGER.debug("Inserting appeal into mongo db for companyId: {}, penaltyReference: {} and " +
            "userId: {}", appeal.getPenaltyIdentifier().getCompanyNumber(), appeal.getPenaltyIdentifier().getPenaltyReference(), userId);

        return Optional.ofNullable(appealRepository.insert(this.appealMapper.map(appeal))).map(AppealEntity::getId).orElseThrow(() ->
            new RuntimeException(String.format("Appeal not saved in database for companyNumber: %s, penaltyReference: %s and userId: %s",
                appeal.getPenaltyIdentifier().getCompanyNumber(), appeal.getPenaltyIdentifier().getPenaltyReference(), userId)));
    }

    private void createContactInChips(Appeal appeal, String userId) {

        final PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
        final ChipsContact chipsContact = buildChipsContact(appeal);

        LOGGER.debug("Creating contact in chips for companyId: {}, penaltyReference: {} and " +
            "userId: {}", penaltyIdentifier.getCompanyNumber(), penaltyIdentifier.getPenaltyReference(), userId);

        try {
            chipsRestClient.createContactInChips(chipsContact, chipsConfiguration.getChipsRestServiceUrl());
        } catch (ChipsServiceException chipsServiceException) {
            LOGGER.debug("Appeal with id {} has failed to create contact", appeal.getId());
            throw chipsServiceException;
        }
    }

    protected ChipsContact buildChipsContact(Appeal appeal) {
        final String companyNumber = appeal.getPenaltyIdentifier().getCompanyNumber();
        final OtherReason otherReason = appeal.getReason().getOther();
        final IllnessReason illnessReason = appeal.getReason().getIllnessReason();

        final ChipsContact chipsContact = new ChipsContact();
        chipsContact.setCompanyNumber(companyNumber);
        chipsContact.setDateReceived(appeal.getCreatedAt().format(DATE_TIME_FORMATTER));
        ReasonType reasonType = appeal.getReason().getReasonType();

        String contactDescription = "Appeal submitted" +
            "\n\nYour reference number is your company number " + companyNumber +
            "\n\nCompany Number: " + companyNumber +
            "\nEmail address: " + appeal.getCreatedBy().getEmailAddress() +
            "\n\nAppeal Reason";

        List<Attachment> attachmentList;

        if(reasonType.getReasonType().equals(ReasonType.OTHER)){
            attachmentList = otherReason.getAttachments();
            contactDescription +=
                ("\nReason: " + otherReason.getTitle() + "\nFurther information: " + otherReason.getDescription());
            contactDescription += ("\nSupporting documents: " + getAttachmentsStr(appeal.getId(), attachmentList));
        } else if(reasonType.getReasonType().equals(ReasonType.ILLNESS)){
            attachmentList = illnessReason.getAttachments();
            contactDescription += ("\nIll Person " + illnessReason.getIllPerson() +
            "\nOther Person: " + illnessReason.getOtherPerson() +
                "\nIllness Start Date: " + illnessReason.getIllnessStart() +
                "\nContinued Illness" + illnessReason.getContinuedIllness() +
                "\nIllness End Date: " + illnessReason.getIllnessEnd() +
                "\nFurther information: " + illnessReason.getIllnessImpactFurtherInformation()
            );
            contactDescription += ("\nSupporting documents: " + getAttachmentsStr(appeal.getId(), attachmentList));
        }

        chipsContact.setContactDescription(contactDescription);
        return chipsContact;
    }

    private String getAttachmentsStr(String appealId, List<Attachment> attachmentList) {
        if (attachmentList == null || attachmentList.isEmpty()) {
            return "None";
        }
        final StringBuilder sb = new StringBuilder();
        attachmentList.forEach(attachment -> {
            sb.append("\n  - ").append(attachment.getName());
            Optional.ofNullable(attachment.getUrl()).ifPresent(url ->
                sb.append("\n    ").append(url).append("&a=").append(appealId));
        });
        return sb.toString();
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id).map(this.appealMapper::map);
    }

    public List<Appeal> getAppealsByPenaltyReference(String penaltyReference){
        return appealRepository.findByPenaltyReference(penaltyReference).stream().map(this.appealMapper::map).collect(Collectors.toList());
    }
}
