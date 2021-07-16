package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.companieshouse.util.TestUtil.createIllnessReason;
import static uk.gov.companieshouse.util.TestUtil.createOtherReason;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.exception.AppealReasonException;
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
}
