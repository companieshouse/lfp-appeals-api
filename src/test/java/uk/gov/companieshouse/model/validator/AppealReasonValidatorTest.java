package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static uk.gov.companieshouse.util.TestUtil.createIllnessReason;
import static uk.gov.companieshouse.util.TestUtil.createOtherReason;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
class AppealReasonValidatorTest {

    @Mock
    private Reason mockReason = new Reason();

    @InjectMocks
    private AppealReasonValidator appealReasonValidator;

    @Test
    void shouldReturnFalseWhenItsNotIllnessOrOtherReason() {
        mockReason = new Reason(null, null);
        assertFalse(appealReasonValidator.isValid(mockReason));
    }

    @Test
    void shouldReturnFalseWhenHasBothIllnessAndOtherReason() {
        mockReason = new Reason(createOtherReason(), createIllnessReason());
        assertFalse(appealReasonValidator.isValid(mockReason));
    }

    @Test
    void shouldReturnTrueWhenItsIllnessReason(){
        mockReason = new Reason(null, createIllnessReason());
        assertTrue(appealReasonValidator.isValid(mockReason));
    }

    @Test
    void shouldReturnTrueWhenWhenItsOtherReason(){
        mockReason = new Reason(createOtherReason(), null);
        assertTrue(appealReasonValidator.isValid(mockReason));
    }
}
