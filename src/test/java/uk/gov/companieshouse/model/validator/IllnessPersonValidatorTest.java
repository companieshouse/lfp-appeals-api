package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;

class IllnessPersonValidatorTest {

    private IllnessPersonValidator illnessPersonValidator;
    private Appeal appeal;

    @BeforeEach
    private void setup() {
        illnessPersonValidator = new IllnessPersonValidator();
        appeal = TestUtil.createAppeal(TestUtil.buildCreatedBy(), TestUtil.createReasonWithIllness());
    }

    @DisplayName("Validation should succeed if illPerson equal someoneElse and otherPerson not empty")
    @Test
    void shouldReturnNullWithIllPersonAndOtherPersonValid(){
        assertNull(illnessPersonValidator.validateIllnessPerson(appeal));
    }

    @DisplayName("Validation should fail if illPerson equal someoneElse and otherPerson is blank")
    @Test
    void shouldReturnOtherPersonEmptyIfOtherPersonIsBlank(){
        appeal.getReason().getIllness().setOtherPerson("    ");
        assertEquals(IllnessPersonValidator.OTHER_PERSON_EMPTY, illnessPersonValidator.validateIllnessPerson(appeal));
    }

    @DisplayName("Validation should fail if illPerson equal someoneElse and otherPerson is blank")
    @Test
    void shouldReturnOtherPersonEmptyIfOtherPersonIsNull(){
        appeal.getReason().getIllness().setOtherPerson(null);
        assertEquals(IllnessPersonValidator.OTHER_PERSON_EMPTY, illnessPersonValidator.validateIllnessPerson(appeal));
    }

    @DisplayName("Validation should fail if illPerson different from illPerson list")
    @Test
    void shouldReturnIllPersonInvalid(){
        appeal.getReason().getIllness().setIllPerson("Something wrong");
        assertEquals(IllnessPersonValidator.ILL_PERSON_INVALID, illnessPersonValidator.validateIllnessPerson(appeal));
    }

    @DisplayName("Validation should fail if illPerson is correct but different " +
                                                            "from someoneElse and otherPerson is not blank")
    @Test
    void shouldReturnOtherPersonInvalid(){
        appeal.getReason().getIllness().setIllPerson("employee");
        appeal.getReason().getIllness().setOtherPerson("something that should not be here");
        assertEquals(IllnessPersonValidator.OTHER_PERSON_INVALID, illnessPersonValidator.validateIllnessPerson(appeal));
    }
}
