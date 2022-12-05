package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);
    private static final String USER_ID = "user_id";
    private static final String APPEAL_ID = "appeal_id";
    private static final String PENALTY_REF = "penalty_reference";
    private static final String COMPANY_NUMBER = "company_number";

    @Autowired
    private AppealMapper appealMapper;
    @Autowired
    private AppealRepository appealRepository;
    @Autowired
    private ChipsRestClient chipsRestClient;
    @Autowired
    private ChipsConfiguration chipsConfiguration;
    @Autowired
    private ChipsContactDescriptionFormatter chipsContactDescriptionFormatter;
    @Autowired
    private EmailService emailService;

    @Transactional
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

		emailService.sendAppealEmails(appeal);
		
        return appealId;
    }

    private String createAppealInMongoDB(Appeal appeal, String userId) {
        LOGGER.debugContext("Inserting appeal into mongo db for companyId: ",
            appeal.getPenaltyIdentifier().getCompanyNumber(), createAppealDebugMap(userId, appeal));

        removeDuplicateRecordsIfFound(appeal);

        return Optional
            .ofNullable(appealRepository.insert(this.appealMapper.map(appeal)))
            .map(AppealEntity::getId)
            .orElseThrow(() ->
                new RuntimeException(String.format("Appeal not saved in database for companyNumber: %s, " +
                        "penaltyReference: %s and userId: %s",
                    appeal.getPenaltyIdentifier().getCompanyNumber(),
                    appeal.getPenaltyIdentifier().getPenaltyReference(),
                    userId)));

    }

    /**
     * Removes duplicate mongo records, if found.
     * @param appeal appeal
     */
    private void removeDuplicateRecordsIfFound(Appeal appeal) {
        final List<AppealEntity> entitiesFound = appealRepository.findByPenaltyReferenceAndCompanyNumber(
            appeal.getPenaltyIdentifier().getPenaltyReference(), appeal.getPenaltyIdentifier().getCompanyNumber());

        if (! entitiesFound.isEmpty()) {
            // entities should not exist in the first place, they are now removed
            LOGGER.info("Removing appeals that should not have been there, number = " + entitiesFound.size());
            entitiesFound.forEach(entity -> appealRepository.delete(entity));
        }
    }

    private void createContactInChips(Appeal appeal, String userId) {

        final ChipsContact chipsContact = chipsContactDescriptionFormatter.buildChipsContact(appeal);

        LOGGER.debugContext("Creating CHIPS contact for companyId: ",
            appeal.getPenaltyIdentifier().getCompanyNumber(), createAppealDebugMap(userId, appeal));

        try {
            chipsRestClient.createContactInChips(chipsContact, chipsConfiguration.getChipsRestServiceUrl());
        } catch (ChipsServiceException chipsServiceException) {
            LOGGER.debug("Appeal with id: " + appeal.getId() + " has failed to create contact", createAppealDebugMap(userId, appeal));
            throw chipsServiceException;
        }
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id).map(this.appealMapper::map);
    }

    public List<Appeal> getAppealsByPenaltyReference(String penaltyReference){
        return appealRepository.findByPenaltyReference(penaltyReference).stream().map(this.appealMapper::map).collect(Collectors.toList());
    }

    public Map<String, Object> createAppealDebugMap(String userId, Appeal appeal){
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(USER_ID, userId);
        debugMap.put(APPEAL_ID, appeal.getId());
        debugMap.put(PENALTY_REF, appeal.getPenaltyIdentifier().getPenaltyReference());
        debugMap.put(COMPANY_NUMBER, appeal.getPenaltyIdentifier().getCompanyNumber());
        return debugMap;
    }

    public Map<String, Object> createDebugMapWithoutAppeal(String appealId){
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(APPEAL_ID, appealId);
        return debugMap;
    }
}
