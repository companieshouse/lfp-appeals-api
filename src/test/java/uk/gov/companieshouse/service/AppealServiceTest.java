package uk.gov.companieshouse.service;

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
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void testCreateAppeal_returnsResourceId() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        assertEquals(TestData.Appeal.id, appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id));
    }

    @Test
    public void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id);

        ArgumentCaptor<AppealEntity> appealArgumentCaptor = ArgumentCaptor.forClass(AppealEntity.class);
        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TestData.Appeal.CreatedBy.id, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(null));

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(null);

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    public void testCreateAppealChipsEnabled_returnsResourceId() {

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        assertEquals(TestData.Appeal.id, appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id));
    }

    @Test
    public void testCreateAppealChipsEnabled_throwsExceptionIfChipsReturnsError() {

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        doThrow(ChipsServiceException.class).when(chipsRestClient).createContactInChips(any(ChipsContact.class), anyString());

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(createAppeal(), TestData.Appeal.CreatedBy.id));

        verify(appealRepository).insert(createAppealEntity(null));
        verify(appealRepository).deleteById(TestData.Appeal.id);
    }

    @Test
    public void testGetAppealById_returnsAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.of(createAppealEntity(TestData.Appeal.id)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal());

        Appeal appeal = appealService.getAppeal(TestData.Appeal.id).orElseThrow();

        assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.Appeal.Reason.OtherReason.title, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.Appeal.Reason.OtherReason.description, appeal.getReason().getOther().getDescription());
        assertEquals(2, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.Appeal.Reason.Attachment.id, appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.Appeal.Reason.Attachment.name, appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.Appeal.Reason.Attachment.contentType, appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.Appeal.Reason.Attachment.size, appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    public void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Appeal> appeal = appealService.getAppeal(TestData.Appeal.id);

        assertFalse(appeal.isPresent());
    }

    @Test
    public void testGetAppealByPenaltyReference_returnsAppeal() {

        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(Optional.of(createAppealEntity(TestData.Appeal.PenaltyIdentifier.penaltyReference)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(createAppeal());

        Appeal appeal = appealService.getAppealByPenaltyReference(TestData.Appeal.PenaltyIdentifier.penaltyReference).orElseThrow();

        assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.Appeal.Reason.OtherReason.title, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.Appeal.Reason.OtherReason.description, appeal.getReason().getOther().getDescription());
        assertEquals(2, appeal.getReason().getOther().getAttachments().size());
        assertEquals(TestData.Appeal.Reason.Attachment.id, appeal.getReason().getOther().getAttachments().get(0).getId());
        assertEquals(TestData.Appeal.Reason.Attachment.name, appeal.getReason().getOther().getAttachments().get(0).getName());
        assertEquals(TestData.Appeal.Reason.Attachment.contentType, appeal.getReason().getOther().getAttachments().get(0).getContentType());
        assertEquals(TestData.Appeal.Reason.Attachment.size, appeal.getReason().getOther().getAttachments().get(0).getSize());
    }

    @Test
    public void testGetAppealByPenaltyReference_returnsEmptyAppeal() {

        when(appealRepository.findByPenaltyReference(any(String.class))).thenReturn(Optional.empty());

        Optional<Appeal> appeal = appealService.getAppealByPenaltyReference(TestData.Appeal.PenaltyIdentifier.penaltyReference);

        assertFalse(appeal.isPresent());
    }

    @Test
    public void testBuildChipsContactWithAttachments() {

        Appeal appeal = createAppeal();
        appeal.setId(TestData.Appeal.id);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithAttachments(), contactDescription);
    }

    private static String expectedContactDescriptionWithAttachments() {
        return "Appeal submitted" +
            "\n\nYour reference number is your company number " + TestData.Appeal.PenaltyIdentifier.companyNumber +
            "\n\nCompany Number: " + TestData.Appeal.PenaltyIdentifier.companyNumber +
            "\nEmail address: " + TestData.Appeal.CreatedBy.email +
            "\n\nAppeal Reason" +
            "\nReason: " + TestData.Appeal.Reason.OtherReason.title +
            "\nFurther information: " + TestData.Appeal.Reason.OtherReason.description +
            "\nSupporting documents: " +
            "\n  - " + TestData.Appeal.Reason.Attachment.name +
            "\n    " + TestData.Appeal.Reason.Attachment.url + "&a=" + TestData.Appeal.id +
            "\n  - " + TestData.Appeal.Reason.Attachment.name;
    }

    @Test
    public void testBuildChipsContactEmptyAttachments() {

        Appeal appeal = createAppeal();
        appeal.setId(TestData.Appeal.id);
        appeal.getReason().getOther().setAttachments(Collections.emptyList());

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
    }

    @Test
    public void testBuildChipsContactNullAttachments() {

        Appeal appeal = createAppeal();
        appeal.setId(TestData.Appeal.id);
        appeal.getReason().getOther().setAttachments(null);

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
    }

    private static String expectedContactDescriptionWithoutAttachments() {
        return "Appeal submitted" +
            "\n\nYour reference number is your company number " + TestData.Appeal.PenaltyIdentifier.companyNumber +
            "\n\nCompany Number: " + TestData.Appeal.PenaltyIdentifier.companyNumber +
            "\nEmail address: " + TestData.Appeal.CreatedBy.email +
            "\n\nAppeal Reason" +
            "\nReason: " + TestData.Appeal.Reason.OtherReason.title +
            "\nFurther information: " + TestData.Appeal.Reason.OtherReason.description +
            "\nSupporting documents: None";
    }

    private Appeal createAppeal() {
        return new Appeal(
            null,
            TestData.Appeal.createdAt,
            new CreatedBy(
                TestData.Appeal.CreatedBy.id,
                TestData.Appeal.CreatedBy.email
            ),
            new PenaltyIdentifier(
                TestData.Appeal.PenaltyIdentifier.companyNumber,
                TestData.Appeal.PenaltyIdentifier.penaltyReference
            ),
            new Reason(new OtherReason(
                TestData.Appeal.Reason.OtherReason.title,
                TestData.Appeal.Reason.OtherReason.description,
                Lists.newArrayList(new Attachment(
                        TestData.Appeal.Reason.Attachment.id,
                        TestData.Appeal.Reason.Attachment.name,
                        TestData.Appeal.Reason.Attachment.contentType,
                        TestData.Appeal.Reason.Attachment.size,
                        TestData.Appeal.Reason.Attachment.url
                    ), new Attachment(
                        TestData.Appeal.Reason.Attachment.id,
                        TestData.Appeal.Reason.Attachment.name,
                        TestData.Appeal.Reason.Attachment.contentType,
                        TestData.Appeal.Reason.Attachment.size,
                        null)
                )))
        );
    }

    private AppealEntity createAppealEntity(String id) {
        return new AppealEntity(
            id,
            TestData.Appeal.createdAt,
            new CreatedByEntity(TestData.Appeal.CreatedBy.id),
            new PenaltyIdentifierEntity(
                TestData.Appeal.PenaltyIdentifier.companyNumber,
                TestData.Appeal.PenaltyIdentifier.penaltyReference
            ),
            new ReasonEntity(
                new OtherReasonEntity(
                    TestData.Appeal.Reason.OtherReason.title,
                    TestData.Appeal.Reason.OtherReason.description,
                    Lists.newArrayList(
                        new AttachmentEntity(
                            TestData.Appeal.Reason.Attachment.id,
                            TestData.Appeal.Reason.Attachment.name,
                            TestData.Appeal.Reason.Attachment.contentType,
                            TestData.Appeal.Reason.Attachment.size
                        )
                    )
                )
            )
        );
    }
}
