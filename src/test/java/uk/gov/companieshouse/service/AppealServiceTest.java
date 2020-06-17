package uk.gov.companieshouse.service;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AppealServiceTest {

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealMapper appealMapper;

    @Mock
    private AppealRepository appealRepository;

    @Test
    public void testCreateAppeal_returnsResourceId() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        assertEquals(TestData.Appeal.id, appealService.saveAppeal(new Appeal(), TestData.Appeal.CreatedBy.id));
    }

    @Test
    public void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(TestData.Appeal.id));

        appealService.saveAppeal(new Appeal(), TestData.Appeal.CreatedBy.id);

        ArgumentCaptor<AppealEntity> appealArgumentCaptor = ArgumentCaptor.forClass(AppealEntity.class);
        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TestData.Appeal.CreatedBy.id, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(createAppealEntity(null));

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(new Appeal(), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        when(appealMapper.map(any(Appeal.class))).thenReturn(createAppealEntity(null));
        when(appealRepository.insert(any(AppealEntity.class))).thenReturn(null);

        String message = assertThrows(Exception.class, () -> appealService.saveAppeal(new Appeal(), TestData.Appeal.CreatedBy.id)).getMessage();
        assertEquals("Appeal not saved in database for companyNumber: 12345678, penaltyReference: A12345678 and userId: USER#1", message);
    }

    @Test
    public void testGetAppealById_returnsAppeal() throws IOException {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.of(createAppealEntity(TestData.Appeal.id)));
        when(appealMapper.map(any(AppealEntity.class))).thenReturn(getValidAppeal());

        Appeal appeal = appealService.getAppeal(TestData.Appeal.id).orElseThrow();

        assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TestData.Appeal.Reason.OtherReason.title, appeal.getReason().getOther().getTitle());
        assertEquals(TestData.Appeal.Reason.OtherReason.description, appeal.getReason().getOther().getDescription());
        assertEquals(1, appeal.getReason().getOther().getAttachments().size());
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

    private Appeal getValidAppeal() {
        return new Appeal(
            null,
            null,
            null,
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
                    TestData.Appeal.Reason.Attachment.size
                ))
            ))
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
