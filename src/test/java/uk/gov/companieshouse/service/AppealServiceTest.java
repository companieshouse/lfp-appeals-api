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

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
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
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.model.ReasonType;
import uk.gov.companieshouse.repository.AppealRepository;

@ExtendWith(MockitoExtension.class)
public class AppealServiceTest {

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
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id, reasonEntity));

        assertEquals(TestData.Appeal.id, appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id));
    }

    @Test
    void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id, reasonEntity));

        appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id);

        ArgumentCaptor<AppealEntity> appealArgumentCaptor = ArgumentCaptor.forClass(AppealEntity.class);
        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TestData.Appeal.CreatedBy.id, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(null, reasonEntity));

        String message = assertThrows(Exception.class,
            () -> appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals(
            "Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1",
            message);
    }

    @Test
    void testCreateAppeal_throwsExceptionIfUnableToInsertData() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(null);

        String message = assertThrows(Exception.class,
            () -> appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals(
            "Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1",
            message);
    }

    @Test
    void testCreateAppealChipsEnabled_returnsResourceId() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id, reasonEntity));

        assertEquals(TestData.Appeal.id, appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id));
    }

    @Test
    void testCreateAppeal_throwsExceptionIfChipsReturnsError() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        doThrow(ChipsServiceException.class).when(chipsRestClient).createContactInChips(any(ChipsContact.class),
            anyString());

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null, reasonEntity));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id, reasonEntity));

        assertThrows(ChipsServiceException.class,
            () -> appealService.saveAppeal(createAppeal(reason), TestData.Appeal.CreatedBy.id));

        verify(appealRepository).insert(createAppealEntity(null, reasonEntity));
    }

    @Test
    void testGetAppealById_returnsAppeal() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findById(any(String.class))).thenReturn(
            Optional.of(createAppealEntity(TestData.Appeal.id, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

        Appeal appeal = appealService.getAppeal(TestData.Appeal.id).orElseThrow();

        assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference,
            appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.Appeal.Reason.OtherReason.title, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.Appeal.Reason.OtherReason.description, appeal.getReason().getOther().getDescription());
        assertEquals(2, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.Appeal.Reason.Attachment.id,
            appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.Appeal.Reason.Attachment.name,
            appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.Appeal.Reason.Attachment.contentType,
            appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.Appeal.Reason.Attachment.size,
            appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Appeal> appeal = appealService.getAppeal(TestData.Appeal.id);

        assertFalse(appeal.isPresent());
    }

    @Test
    void testGetAppealsByPenaltyReference_returnsListOfAppeals() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(createAppealEntity(TestData.Appeal.PenaltyIdentifier.penaltyReference, reasonEntity)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(
            TestData.Appeal.PenaltyIdentifier.penaltyReference);
        Appeal appeal = appealList.get(0);

        assertEquals(1, appealList.size());

        assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference,
            appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.Appeal.Reason.OtherReason.title, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.Appeal.Reason.OtherReason.description, appeal.getReason().getOther().getDescription());
        assertEquals(2, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.Appeal.Reason.Attachment.id,
            appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.Appeal.Reason.Attachment.name,
            appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.Appeal.Reason.Attachment.contentType,
            appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.Appeal.Reason.Attachment.size,
            appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    void testGetMultipleAppealByPenaltyReference_returnsListOfAppeals() {
        ReasonEntity reasonEntity = createReasonEntityWithOther();
        Reason reason = createReasonWithOther();
        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(
            List.of(createAppealEntity(TestData.Appeal.PenaltyIdentifier.penaltyReference, reasonEntity),
                createAppealEntity(TestData.Appeal.PenaltyIdentifier.penaltyReference, reasonEntity)));

        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal(reason));

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(
            TestData.Appeal.PenaltyIdentifier.penaltyReference);

        assertEquals(2, appealList.size());

    }

    @Test
    void testGetAppealsByPenaltyReference_returnsEmptyListOfAppeals() {

        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(List.of());

        List<Appeal> appealList = appealService.getAppealsByPenaltyReference(
            TestData.Appeal.PenaltyIdentifier.penaltyReference);

        assertTrue(appealList.isEmpty());
    }

    @Test
    void testBuildChipsContactOtherReasonWithAttachments() {
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.Appeal.id);

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
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\n\nCompany Number: "
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\nEmail address: "
            + TestData.Appeal.CreatedBy.email
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.Appeal.Reason.OtherReason.title
            + "\nFurther information: "
            + TestData.Appeal.Reason.OtherReason.description
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.Appeal.Reason.Attachment.name
            + "\n    "
            + TestData.Appeal.Reason.Attachment.url
            + "&a="
            + TestData.Appeal.id
            + "\n  - "
            + TestData.Appeal.Reason.Attachment.name;
    }

    @Test
    void testBuildChipsContactOtherReasonEmptyAttachments() {
        Reason reason = createReasonWithOther();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.Appeal.id);
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
        appeal.setId(TestData.Appeal.id);
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
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\n\nCompany Number: "
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\nEmail address: "
            + TestData.Appeal.CreatedBy.email
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.Appeal.Reason.OtherReason.title
            + "\nFurther information: "
            + TestData.Appeal.Reason.OtherReason.description
            + "\nSupporting documents: None";
    }

    @Test
    void testBuildChipsContactIllnessReasonWithAttachments() {
        Reason reason = createReasonWithIllness();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.Appeal.id);

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
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\n\nCompany Number: "
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\nEmail address: "
            + TestData.Appeal.CreatedBy.email
            + "\n\nAppeal Reason"
            + "\nIll Person "
            + TestData.Appeal.Reason.IllnessReason.illPerson
            + "\nOther Person: "
            + TestData.Appeal.Reason.IllnessReason.otherPerson
            + "\nIllness Start Date: "
            + TestData.Appeal.Reason.IllnessReason.illnessStart
            + "\nContinued Illness"
            + TestData.Appeal.Reason.IllnessReason.continuedIllness
            + "\nIllness End Date: "
            + TestData.Appeal.Reason.IllnessReason.illnessEnd
            + "\nFurther information: "
            + TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.Appeal.Reason.Attachment.name
            + "\n    "
            + TestData.Appeal.Reason.Attachment.url
            + "&a="
            + TestData.Appeal.id
            + "\n  - "
            + TestData.Appeal.Reason.Attachment.name;
    }

    @Test
    void testBuildChipsContactIllnessReasonWithoutAttachments() {
        Reason reason = createReasonWithIllness();
        Appeal appeal = createAppeal(reason);
        appeal.setId(TestData.Appeal.id);
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
        appeal.setId(TestData.Appeal.id);
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
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\n\nCompany Number: "
            + TestData.Appeal.PenaltyIdentifier.companyNumber
            + "\nEmail address: "
            + TestData.Appeal.CreatedBy.email
            + "\n\nAppeal Reason"
            + "\nIll Person "
            + TestData.Appeal.Reason.IllnessReason.illPerson
            + "\nOther Person: "
            + TestData.Appeal.Reason.IllnessReason.otherPerson
            + "\nIllness Start Date: "
            + TestData.Appeal.Reason.IllnessReason.illnessStart
            + "\nContinued Illness"
            + TestData.Appeal.Reason.IllnessReason.continuedIllness
            + "\nIllness End Date: "
            + TestData.Appeal.Reason.IllnessReason.illnessEnd
            + "\nFurther information: "
            + TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation
            + "\nSupporting documents: None";
    }

    private OtherReason createOtherReason() {
        return new OtherReason(TestData.Appeal.Reason.OtherReason.title, TestData.Appeal.Reason.OtherReason.description,
            Lists.newArrayList(
                new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size,
                    TestData.Appeal.Reason.Attachment.url),
                new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size, null)));
    }

    private IllnessReason createIllnessReason() {
        return new IllnessReason(TestData.Appeal.Reason.IllnessReason.illPerson,
            TestData.Appeal.Reason.IllnessReason.otherPerson, TestData.Appeal.Reason.IllnessReason.illnessStart,
            TestData.Appeal.Reason.IllnessReason.continuedIllness, TestData.Appeal.Reason.IllnessReason.illnessEnd,
            TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation, Lists.newArrayList(
            new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size,
                TestData.Appeal.Reason.Attachment.url),
            new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size, null)));
    }

    private OtherReasonEntity createOtherReasonEntity() {
        return new OtherReasonEntity(TestData.Appeal.Reason.OtherReason.title, TestData.Appeal.Reason.OtherReason.description,
            Lists.newArrayList(
                new AttachmentEntity(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size)));
    }

    private Reason createReasonWithOther() {
        return new Reason(createOtherReason(), null);
    }

    private Reason createReasonWithIllness(){
        return new Reason(null, createIllnessReason());
    }

    private ReasonEntity createReasonEntityWithOther() {
        return new ReasonEntity(createOtherReasonEntity(), null);
    }

    private Appeal createAppeal(Reason reason) {
        return new Appeal(null, TestData.Appeal.createdAt,
            new CreatedBy(TestData.Appeal.CreatedBy.id, TestData.Appeal.CreatedBy.email),
            new PenaltyIdentifier(TestData.Appeal.PenaltyIdentifier.companyNumber,
                TestData.Appeal.PenaltyIdentifier.penaltyReference), reason);

    }

    private AppealEntity createAppealEntity(String id, ReasonEntity reasonEntity) {
        return new AppealEntity(id, TestData.Appeal.createdAt, new CreatedByEntity(TestData.Appeal.CreatedBy.id),
            new PenaltyIdentifierEntity(TestData.Appeal.PenaltyIdentifier.companyNumber,
                TestData.Appeal.PenaltyIdentifier.penaltyReference), reasonEntity);

    }
}
