package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestUtil.createIllnessReason;
import static uk.gov.companieshouse.TestUtil.createOtherReason;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
class AppealReasonValidatorTest {

    @InjectMocks
    private AppealReasonValidator appealReasonValidator;

    @DisplayName("Validation failure for no Appeal reasons")
    @Test
    void shouldReturnFalseWhenItsNotIllnessOrOtherReason() {
        Reason mockReason = new Reason();
        assertEquals("Other/Illness must be supplied with an Appeal",appealReasonValidator.validate(mockReason));
    }

    @DisplayName("Validation failure for more than one Appeal reason")
    @Test
    void shouldReturnFalseWhenHasBothIllnessAndOtherReason() {
        Reason mockReason = new Reason();
        mockReason.setIllness(createIllnessReason());
        mockReason.setOther(createOtherReason());
        assertEquals("Only one reason type can be supplied with an Appeal",appealReasonValidator.validate(mockReason));
    }

    @DisplayName("Validation success for illness Appeal reason only")
    @Test
    void shouldReturnTrueWhenItsIllnessReason(){
        Reason mockReason = new Reason();
        mockReason.setIllness(createIllnessReason());
        assertNull(appealReasonValidator.validate(mockReason));
    }

    @DisplayName("Validation success for other Appeal reason only")
    @Test
    void shouldReturnTrueWhenWhenItsOtherReason(){
        Reason mockReason = new Reason();
        mockReason.setOther(createOtherReason());
        assertNull(appealReasonValidator.validate(mockReason));
    }
}
