package uk.gov.companieshouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;
import uk.gov.companieshouse.util.ChipsContactDescriptionFormatter;

@ExtendWith(MockitoExtension.class)
class AppealServiceTest {

    private static final String TEST_CHIPS_URL = "http://someurl";

    private static final ChipsContact CONTACT = new ChipsContact();

    private static final String USER_ID = "user_id";
    private static final String APPEAL_ID = "appeal_id";
    private static final String PENALTY_REF = "penalty_reference";
    private static final String COMPANY_NUMBER = "company_number";

    @Mock
    private ChipsContactDescriptionFormatter chipsContactDescriptionFormatter;

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealMapper appealMapper;

    @Mock
    private AppealRepository appealRepository;

    @Mock
    private ChipsRestClient chipsRestClient;

    @Mock
    private ChipsConfiguration chipsConfiguration;

    @Mock
    private EmailService emailService;

    @Test
    void testCreateAppeal_returnsResourceId() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(TestUtil.createAppealEntity(TestData.ID, createdByEntity, reasonEntity));

        assertEquals(TestData.ID, appealService.saveAppeal(TestUtil.createAppeal(createdBy, reason), TestData.USER_ID));
    }

    @Test
    void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(TestUtil.createAppealEntity(TestData.ID, createdByEntity, reasonEntity));

        appealService.saveAppeal(TestUtil.createAppeal(createdBy, reason), TestData.USER_ID);

        ArgumentCaptor<AppealEntity> appealArgumentCaptor = ArgumentCaptor.forClass(AppealEntity.class);
        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TestData.USER_ID, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(TestUtil.createAppeal(createdBy, reason), TestData.USER_ID)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    void testCreateAppeal_throwsExceptionIfUnableToInsertData() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(null);

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(TestUtil.createAppeal(createdBy, reason), TestData.USER_ID)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    void testCreateAppealChipsEnabled_returnsResourceId() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);
        when(chipsContactDescriptionFormatter.buildChipsContact(any(Appeal.class))).thenReturn(CONTACT);

        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(TestUtil.createAppealEntity(TestData.ID, createdByEntity, reasonEntity));

        assertEquals(TestData.ID, appealService.saveAppeal(TestUtil.createAppeal(createdBy, reason), TestData.USER_ID));
    }

    @Test
    void testCreateAppeal_throwsExceptionIfChipsReturnsError() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);
        when(chipsContactDescriptionFormatter.buildChipsContact(appeal)).thenReturn(CONTACT);

        doThrow(ChipsServiceException.class).when(chipsRestClient).createContactInChips(CONTACT, TEST_CHIPS_URL);

        when(appealMapper.map(any(Appeal.class))).thenReturn(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(TestUtil.createAppealEntity(TestData.ID, createdByEntity, reasonEntity));

        assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(appeal, TestData.USER_ID));

        verify(appealRepository).insert(TestUtil.createAppealEntity(null, createdByEntity, reasonEntity));
    }

    @Test
    void testGetAppealById_returnsAppeal() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealRepository.findById(any(String.class))).thenReturn(
            Optional.of(TestUtil.createAppealEntity(TestData.ID, createdByEntity, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(TestUtil.createAppeal(createdBy, reason));

        Appeal appeal = appealService.getAppeal(TestData.ID).orElseThrow();

        assertEquals(TestData.COMPANY_NUMBER, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.PENALTY_REFERENCE, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.TITLE, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.DESCRIPTION, appeal.getReason().getOther().getDescription());
        assertEquals(1, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.ATTACHMENT_ID, appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.ATTACHMENT_NAME, appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.CONTENT_TYPE, appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.ATTACHMENT_SIZE, appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Appeal> appeal = appealService.getAppeal(TestData.ID);

        assertFalse(appeal.isPresent());
    }

    @Test
    void testGetAppealsByPenaltyReference_returnsListOfAppeals() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(TestUtil.createAppealEntity(TestData.PENALTY_REFERENCE, createdByEntity, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(TestUtil.createAppeal(createdBy, reason));

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(
            TestData.PENALTY_REFERENCE);
        Appeal appeal = appealList.get(0);

        assertEquals(1, appealList.size());

        assertEquals(TestData.PENALTY_REFERENCE, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.COMPANY_NUMBER, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.TITLE, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.DESCRIPTION, appeal.getReason().getOther().getDescription());
        assertEquals(1, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.ATTACHMENT_ID, appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.ATTACHMENT_NAME, appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.CONTENT_TYPE, appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.ATTACHMENT_SIZE, appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    void testGetMultipleAppealByPenaltyReference_returnsListOfAppeals() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        CreatedByEntity createdByEntity = TestUtil.buildCreatedByEntity();
        ReasonEntity reasonEntity = TestUtil.createReasonEntityWithOther();
        Reason reason = TestUtil.createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(TestUtil.createAppealEntity(TestData.PENALTY_REFERENCE, createdByEntity, reasonEntity),
                TestUtil.createAppealEntity(TestData.PENALTY_REFERENCE, createdByEntity, reasonEntity)));

        when(appealMapper.map(any(AppealEntity.class))).thenReturn(TestUtil.createAppeal(createdBy,reason));

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(TestData.PENALTY_REFERENCE);

        assertEquals(2, appealList.size());

    }

    @Test
    void testGetAppealsByPenaltyReference_returnsEmptyListOfAppeals() {

        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(List.of());

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(TestData.PENALTY_REFERENCE);

        assertTrue(appealList.isEmpty());
    }

    @Test
    void testCreateDebugMapWithAppeal_returnsMapWithAppealDetails() {
        Map<String, Object> returnedMap = appealService.createAppealDebugMap(TestData.USER_ID,
            TestUtil.createAppeal(TestUtil.buildCreatedBy(), TestUtil.createReasonWithOther()));
        assertEquals(4, returnedMap.size());
        assertEquals(TestData.ID, returnedMap.get(APPEAL_ID));
        assertEquals(TestData.USER_ID, returnedMap.get(USER_ID));
        assertEquals(TestData.PENALTY_REFERENCE, returnedMap.get(PENALTY_REF));
        assertEquals(TestData.COMPANY_NUMBER, returnedMap.get(COMPANY_NUMBER));
    }

    @Test
    void testCreateDebugMapWithoutAppeal_retursnMapWithID(){
        Map<String, Object> returnedMap = appealService.createDebugMapWithoutAppeal(TestData.ID);
        assertEquals(TestData.ID, returnedMap.get(APPEAL_ID));
    }

}
