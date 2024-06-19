package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.repository.AppealRepository;
import uk.gov.companieshouse.util.ChipsContactDescriptionFormatter;

@Service
public class AppealService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);
    private static final String USER_ID = "user_id";
    private static final String APPEAL_ID = "appeal_id";
    private static final String PENALTY_REF = "penalty_reference";
    private static final String COMPANY_NUMBER = "company_number";

    @Autowired
     AppealMapper appealMapper;
    @Autowired
     AppealRepository appealRepository;
    @Autowired
     ChipsRestClient chipsRestClient;
    @Autowired
     ChipsConfiguration chipsConfiguration;
    @Autowired
     ChipsContactDescriptionFormatter chipsContactDescriptionFormatter;
    @Autowired
    EmailService emailService;

    public String saveAppeal(Appeal appeal, String userId) {
        appeal.setCreatedAt(LocalDateTime.now());
        appeal.getCreatedBy().setId(userId);

        String appealId = createAppealInMongoDB(appeal, userId);
        appeal.setId(appealId);

        if (chipsConfiguration.isChipsEnabled()) {
            createContactInChips(appeal, userId);
        } else {
            LOGGER.debugContext(appeal.getPenaltyIdentifier().getPenaltyReference(), "CHIPS feature is disabled", createDebugMapAppeal(userId, appeal));
        }

        emailService.sendAppealEmails(appeal);

        return appealId;
    }

    private String createAppealInMongoDB(Appeal appeal, String userId) {
        String companyNumber = appeal.getPenaltyIdentifier().getCompanyNumber();
        String penaltyReference = appeal.getPenaltyIdentifier().getPenaltyReference();

        LOGGER.debugContext(penaltyReference, "Creating appeal in mongo db",
                createDebugMapAppeal(userId, appeal));

        final String appealId;
        List<AppealEntity> queryResult = appealRepository
                .findByCompanyNumberPenaltyReference(companyNumber, penaltyReference);
        LOGGER.infoContext(penaltyReference,
                "Is there an appeal for this company/reference? " + queryResult.isEmpty(), null);

        if (queryResult.isEmpty()) {
            appealId = Optional.ofNullable(appealRepository.insert(this.appealMapper.map(appeal)))
                    .map(AppealEntity::getId)
                    .orElseThrow(() -> new RuntimeException(String.format(
                            "Appeal not created in database for companyNumber: %s, penaltyReference: %s and userId: %s",
                            appeal.getPenaltyIdentifier().getCompanyNumber(),
                            appeal.getPenaltyIdentifier().getPenaltyReference(), userId)));
        } else {
            LOGGER.infoContext(penaltyReference,
                    "Update existing appeal record with reason: " + queryResult.isEmpty(), null);
            var updatedAppeal = this.appealMapper.map(queryResult.get(0));
            updatedAppeal.setReason(appeal.getReason());
            updatedAppeal.setCreatedAt(LocalDateTime.now());

            appealId = Optional
                    .ofNullable(appealRepository.save(this.appealMapper.map(updatedAppeal)))
                    .map(AppealEntity::getId)
                    .orElseThrow(() -> new RuntimeException(String.format(
                            "Appeal not updated in database for companyNumber: %s, penaltyReference: %s and userId: %s",
                            appeal.getPenaltyIdentifier().getCompanyNumber(),
                            appeal.getPenaltyIdentifier().getPenaltyReference(), userId)));
        }

        return appealId;
    }

    private void createContactInChips(Appeal appeal, String userId) {

        String penaltyReference = appeal.getPenaltyIdentifier().getPenaltyReference();

        final ChipsContact chipsContact = chipsContactDescriptionFormatter.buildChipsContact(appeal);

        LOGGER.debugContext(penaltyReference, "Creating CHIPS contact", createDebugMapAppeal(userId, appeal));

        try {
            chipsRestClient.createContactInChips(chipsContact, chipsConfiguration.getChipsRestServiceUrl());
        } catch (ChipsServiceException chipsServiceException) {
            LOGGER.errorContext(penaltyReference, "Appeal with id: " + appeal.getId() + " has failed to create contact", chipsServiceException, createDebugMapAppeal(userId, appeal));
            throw chipsServiceException;
        }
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id).map(this.appealMapper::map);
    }

    public List<Appeal> getAppealsByPenaltyReference(String companyNumber, String penaltyReference){
        return appealRepository.findByCompanyNumberPenaltyReference(companyNumber, penaltyReference).stream().map(this.appealMapper::map).collect(Collectors.toList());
    }

    public Map<String, Object> createDebugMapAppeal(String userId, Appeal appeal){
        final Map<String, Object> debugMap = new LinkedHashMap<>();
        debugMap.put(USER_ID, userId);
        debugMap.put(APPEAL_ID, appeal.getId());
        debugMap.put(COMPANY_NUMBER, appeal.getPenaltyIdentifier().getCompanyNumber());
        debugMap.put(PENALTY_REF, appeal.getPenaltyIdentifier().getPenaltyReference());
        return debugMap;
    }

    public Map<String, Object> createDebugMapWithoutAppeal(String appealId){
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(APPEAL_ID, appealId);
        return debugMap;
    }

    public Map<String, Object> createDebugMapAppealSearch(String companyNumber, String penaltyReference){
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(COMPANY_NUMBER, companyNumber);
        debugMap.put(PENALTY_REF, penaltyReference);
        return debugMap;
    }
}
