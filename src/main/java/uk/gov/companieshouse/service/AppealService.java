package uk.gov.companieshouse.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.repository.AppealRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AppealService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AppealService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AppealRepository appealRepository;
    private final ChipsRestClient chipsRestClient;
    private final ChipsConfiguration chipsConfiguration;

    @Autowired
    public AppealService(AppealRepository appealRepository, ChipsRestClient chipsRestClient, ChipsConfiguration chipsConfiguration) {
        this.appealRepository = appealRepository;
        this.chipsRestClient = chipsRestClient;
        this.chipsConfiguration = chipsConfiguration;
    }

    public String saveAppeal(Appeal appeal, String userId) throws Exception {

        final CreatedBy createdBy = new CreatedBy();
        createdBy.setId(userId);
        appeal.setCreatedBy(createdBy);

        final LocalDateTime createdAt = LocalDateTime.now();
        appeal.setCreatedAt(createdAt);

        String appealId = createAppealInMongoDB(appeal, userId);

        if (chipsConfiguration.isChipsEnabled()) {
            createContactInChips(appeal, userId, appealId);
        } else {
            LOGGER.debug("CHIPS feature is disabled");
        }

        return appealId;
    }

    private String createAppealInMongoDB(Appeal appeal, String userId) throws Exception {

        final PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();

        LOGGER.debug("Inserting appeal into mongo db for companyId: {}, penaltyReference: {} and " +
            "userId: {}", penaltyIdentifier.getCompanyNumber(), penaltyIdentifier.getPenaltyReference(), userId);

        return Optional.ofNullable(appealRepository.insert(appeal)).map(Appeal::getId).orElseThrow(() ->
            new Exception(
                String.format("Appeal not saved in database for companyId: %s, penaltyReference: %s and userId: %s",
                    penaltyIdentifier.getCompanyNumber(), penaltyIdentifier.getPenaltyReference(), userId)));
    }

    private void createContactInChips(Appeal appeal, String userId, String id) {

        final PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
        final ChipsContact chipsContact = buildChipsContact(appeal);

        LOGGER.debug("Creating contact in chips for companyId: {}, penaltyReference: {} and " +
            "userId: {}", penaltyIdentifier.getCompanyNumber(), penaltyIdentifier.getPenaltyReference(), userId);

        ResponseEntity<String> responseEntity = chipsRestClient.createContactInChips(chipsContact,
            chipsConfiguration.getChipsRestServiceUrl());

        if (responseEntity == null || !responseEntity.getStatusCode().is2xxSuccessful()) {
            LOGGER.debug("Deleting appeal with id {} from mongodb", id);
            appealRepository.deleteById(id);
            throw new ChipsServiceException(String.format("Contact cannot be created in chips for companyId: %s, " +
                    "penaltyReference: %s and userId: %s", penaltyIdentifier.getCompanyNumber(),
                penaltyIdentifier.getPenaltyReference(), userId));
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
            "\nEmail address: " +
            "\n\nAppeal Reason" +
            "\nReason: " + otherReason.getTitle() +
            "\nFurther information: " + otherReason.getDescription() +
            "\nSupporting documents: None";

        chipsContact.setContactDescription(contactDescription);

        return chipsContact;
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id);
    }
}
