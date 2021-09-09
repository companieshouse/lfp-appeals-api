package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestUtil.createIllnessReason;
import static uk.gov.companieshouse.TestUtil.createOtherReason;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.model.Reason;

class AppealReasonValidatorTest {

    private AppealReasonValidator appealReasonValidator;

    @BeforeEach
    private void setup() {
        appealReasonValidator = new AppealReasonValidator();
    }

    @DisplayName("Validation failure for no Appeal reasons")
    @Test
    void shouldReturnFalseWhenItsNotIllnessOrOtherReason() {
        Reason reason = new Reason();
        assertEquals("Other/Illness must be supplied with an Appeal",appealReasonValidator.validate(reason));
    }

    @DisplayName("Validation failure for more than one Appeal reason")
    @Test
    void shouldReturnFalseWhenHasBothIllnessAndOtherReason() {
        Reason reason = new Reason();
        reason.setIllness(createIllnessReason());
        reason.setOther(createOtherReason());
        assertEquals("Only one reason type can be supplied with an Appeal",appealReasonValidator.validate(reason));
    }

    @DisplayName("Validation success for illness Appeal reason only")
    @Test
    void shouldReturnTrueWhenItsIllnessReason(){
        Reason reason = new Reason();
        reason.setIllness(createIllnessReason());
        assertNull(appealReasonValidator.validate(reason));
    }

    @DisplayName("Validation success for other Appeal reason only")
    @Test
    void shouldReturnTrueWhenWhenItsOtherReason(){
        Reason reason = new Reason();
        reason.setOther(createOtherReason());
        assertNull(appealReasonValidator.validate(reason));
    }
}
