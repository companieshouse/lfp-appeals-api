package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.exception.AppealReasonException;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
class AppealReasonValidatorTest {

    @Mock
    private Reason mockReason = new Reason();

    @InjectMocks
    private AppealReasonValidator appealReasonValidator;

    @Test
    void shouldThrowAnExceptionWhenItsNotIllnessOrOtherReason() {
        assertThrows(AppealReasonException.class, () -> appealReasonValidator.validate(mockReason));
    }

    @Test
    void shouldThrowAnExceptionWhenHasBothIllnessAndOtherReason() {
        mockReason = new Reason(createOtherReason(), createIllnessReason());
        assertThrows(AppealReasonException.class, () -> appealReasonValidator.validate(mockReason));
    }

    @Test
    void shouldNotThrowErrorWhenItsIllnessReason(){
        mockReason = new Reason(null, createIllnessReason());
        assertDoesNotThrow(() -> appealReasonValidator.validate(mockReason));
    }

    @Test
    void shouldNotThrowErrorWhenItsOtherReason(){
        mockReason = new Reason(createOtherReason(), null);
        assertDoesNotThrow(() -> appealReasonValidator.validate(mockReason));
    }

    private IllnessReason createIllnessReason(){
        return new IllnessReason(
            TestData.Appeal.Reason.IllnessReason.illPerson,
            TestData.Appeal.Reason.IllnessReason.otherPerson,
            TestData.Appeal.Reason.IllnessReason.illnessStart,
            TestData.Appeal.Reason.IllnessReason.continuedIllness,
            TestData.Appeal.Reason.IllnessReason.illnessEnd,
            TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation,
            Lists.newArrayList(
                new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size,
                    TestData.Appeal.Reason.Attachment.url)
            ));
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
}
