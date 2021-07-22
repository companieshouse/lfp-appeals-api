package uk.gov.companieshouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.util.TestUtil.createIllnessReason;
import static uk.gov.companieshouse.util.TestUtil.createOtherReason;
import static uk.gov.companieshouse.util.TestUtil.createOtherReasonEntity;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.model.ReasonType;
import uk.gov.companieshouse.repository.AppealRepository;

@ExtendWith(MockitoExtension.class)
class AppealServiceTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String TEST_CHIPS_URL = "http://someurl";

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

    @Test
    void testCreateAppeal_returnsResourceId() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.ID, reasonEntity));

        assertEquals(TestData.ID, appealService.saveAppeal(createAppeal(reason), TestData.USER_ID));
    }

    @Test
    void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.ID, reasonEntity));

        appealService.saveAppeal(createAppeal(reason), TestData.USER_ID);

        ArgumentCaptor<AppealEntity> appealArgumentCaptor = ArgumentCaptor.forClass(AppealEntity.class);
        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TestData.USER_ID, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(null, reasonEntity));

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(reason), TestData.USER_ID)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    void testCreateAppeal_throwsExceptionIfUnableToInsertData() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(null);

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(reason), TestData.USER_ID)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    void testCreateAppealChipsEnabled_returnsResourceId() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.ID, reasonEntity));

        assertEquals(TestData.ID, appealService.saveAppeal(createAppeal(reason), TestData.USER_ID));
    }

    @Test
    void testCreateAppeal_throwsExceptionIfChipsReturnsError() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        doThrow(ChipsServiceException.class).when(chipsRestClient).createContactInChips(any(ChipsContact.class),
            anyString());

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.ID, reasonEntity));

        assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(appeal, TestData.USER_ID));

        verify(appealRepository).insert(createAppealEntity(null, reasonEntity));
    }

    @Test
    void testGetAppealById_returnsAppeal() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findById(any(String.class))).thenReturn(
            Optional.of(createAppealEntity(TestData.ID, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

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
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(createAppealEntity(TestData.PENALTY_REFERENCE, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

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
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(createAppealEntity(TestData.PENALTY_REFERENCE, reasonEntity),
                createAppealEntity(TestData.PENALTY_REFERENCE, reasonEntity)));

        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

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
    void testBuildChipsContactOtherReasonWithAttachments() {
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionOtherReasonWithAttachments(), contactDescription);
        assertEquals(ReasonType.OTHER, reason.getReasonType().getReasonType());
    }

    private static String expectedContactDescriptionOtherReasonWithAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.TITLE
            + "\nFurther information: "
            + TestData.DESCRIPTION
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.ATTACHMENT_NAME
            + "\n    "
            + TestData.ATTACHMENT_URL
            + "&a="
            + TestData.ID;
    }

    @Test
    void testBuildChipsContactOtherReasonEmptyAttachments() {
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getOther().setAttachments(Collections.emptyList());

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
        assertEquals(ReasonType.OTHER, reason.getReasonType().getReasonType());
    }

    @Test
    void testBuildChipsContactOtherReasonsNullAttachments() {
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getOther().setAttachments(null);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
        assertEquals(ReasonType.OTHER, reason.getReasonType().getReasonType());
    }

    private static String expectedContactDescriptionWithoutAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.TITLE
            + "\nFurther information: "
            + TestData.DESCRIPTION
            + "\nSupporting documents: None";
    }

    @Test
    void testBuildChipsContactIllnessReasonWithAttachments() {
        Reason reason = createReasonWithIllness();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithAttachments(), contactDescription);
        assertEquals(ReasonType.ILLNESS, reason.getReasonType().getReasonType());
    }

    private static String expectedContactDescriptionIllnessReasonWithAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nIll Person "
            + TestData.ILL_PERSON
            + "\nOther Person: "
            + TestData.OTHER_PERSON
            + "\nIllness Start Date: "
            + TestData.ILLNESS_START
            + "\nContinued Illness"
            + TestData.CONTINUED_ILLNESS
            + "\nIllness End Date: "
            + TestData.ILLNESS_END
            + "\nFurther information: "
            + TestData.ILLNESS_IMPACT_FURTHER_INFORMATION
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.ATTACHMENT_NAME
            + "\n    "
            + TestData.ATTACHMENT_URL
            + "&a="
            + TestData.ID;
    }

    @Test
    void testBuildChipsContactIllnessReasonWithoutAttachments() {
        Reason reason = createReasonWithIllness();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getIllnessReason().setAttachments(Collections.emptyList());

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithoutAttachments(), contactDescription);
        assertEquals(ReasonType.ILLNESS, reason.getReasonType().getReasonType());
    }

    @Test
    void testBuildChipsContactIllnessReasonWithNullAttachments() {
        Reason reason = createReasonWithIllness();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getIllnessReason().setAttachments(null);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithoutAttachments(), contactDescription);
        assertEquals(ReasonType.ILLNESS, reason.getReasonType().getReasonType());
    }

    private static String expectedContactDescriptionIllnessReasonWithoutAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nIll Person "
            + TestData.ILL_PERSON
            + "\nOther Person: "
            + TestData.OTHER_PERSON
            + "\nIllness Start Date: "
            + TestData.ILLNESS_START
            + "\nContinued Illness"
            + TestData.CONTINUED_ILLNESS
            + "\nIllness End Date: "
            + TestData.ILLNESS_END
            + "\nFurther information: "
            + TestData.ILLNESS_IMPACT_FURTHER_INFORMATION
            + "\nSupporting documents: None";
    }


    private Reason createReasonWithOther() {
        Reason reason = new Reason();
        reason.setOther(createOtherReason());
        return reason;
    }

    private Reason createReasonWithIllness(){
        Reason reason = new Reason();
        reason.setIllnessReason(createIllnessReason());
        return reason;
    }

    private ReasonEntity createReasonEntityWithOther() {
        ReasonEntity reasonEntity = new ReasonEntity();
        reasonEntity.setOther(createOtherReasonEntity());
        return reasonEntity;
    }

    private Appeal createAppeal(Reason reason) {
        return new Appeal(null, TestData.CREATED_AT,
            new CreatedBy(TestData.USER_ID, TestData.EMAIL),
            new PenaltyIdentifier(TestData.COMPANY_NUMBER,
                TestData.PENALTY_REFERENCE), reason);

    }

    private AppealEntity createAppealEntity(String id, ReasonEntity reasonEntity) {
        return new AppealEntity(id, TestData.CREATED_AT, new CreatedByEntity(TestData.USER_ID),
            new PenaltyIdentifierEntity(TestData.COMPANY_NUMBER,
                TestData.PENALTY_REFERENCE), reasonEntity);

    }
}
