package uk.gov.companieshouse.service;

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
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.repository.AppealRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

        if (chipsConfiguration.isChipsEnabled()) {
            createContactInChips(appeal, userId, appealId);
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

    private void createContactInChips(Appeal appeal, String userId, String id) {

        final PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
        final ChipsContact chipsContact = buildChipsContact(appeal);

        LOGGER.debug("Creating contact in chips for companyId: {}, penaltyReference: {} and " +
            "userId: {}", penaltyIdentifier.getCompanyNumber(), penaltyIdentifier.getPenaltyReference(), userId);

        try {
            chipsRestClient.createContactInChips(chipsContact, chipsConfiguration.getChipsRestServiceUrl());
        } catch (ChipsServiceException chipsServiceException) {
            LOGGER.debug("Deleting appeal with id {} from mongodb", id);
            appealRepository.deleteById(id);
            throw chipsServiceException;
        }
    }

    protected ChipsContact buildChipsContact(Appeal appeal) {

        final String companyNumber = appeal.getPenaltyIdentifier().getCompanyNumber();
        final OtherReason otherReason = appeal.getReason().getOther();

        final ChipsContact chipsContact = new ChipsContact();
        chipsContact.setCompanyNumber(companyNumber);
        chipsContact.setDateReceived(appeal.getCreatedAt().format(DATE_TIME_FORMATTER));

        final String contactDescription = "Appeal submitted" +
            "\n\nYour reference number is your company number " + companyNumber +
            "\n\nCompany Number: " + companyNumber +
            "\nEmail address: " + appeal.getCreatedBy().getEmailAddress() +
            "\n\nAppeal Reason" +
            "\nReason: " + otherReason.getTitle() +
            "\nFurther information: " + otherReason.getDescription() +
            "\nSupporting documents: None";

        chipsContact.setContactDescription(contactDescription);

        return chipsContact;
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id).map(this.appealMapper::map);
    }
}
