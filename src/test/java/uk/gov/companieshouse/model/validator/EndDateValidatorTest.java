package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;

class EndDateValidatorTest
{

    private EndDateValidator endDateValidator;
    private Appeal appeal = new Appeal();

    @BeforeEach
    void setup() {
        endDateValidator = new EndDateValidator();
        appeal = TestUtil.createAppeal(TestUtil.buildCreatedBy(), TestUtil.createReasonWithIllness());
    }

    @DisplayName("Validation failure for empty end date")
    @Test
    void shouldReturnStringIfEndDateIsEmpty(){
        appeal.getReason().getIllness().setIllnessEnd("");
        appeal.getReason().getIllness().setContinuedIllness(false);
        assertEquals(EndDateValidator.EMPTY_END_DATE, endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation success if end date is null and continued is true")
    @Test
    void shouldReturnNullIfEndDateIsNullAndContinuedTrue() {
        appeal.getReason().getIllness().setIllnessEnd(null);
        appeal.getReason().getIllness().setContinuedIllness(true);
        assertNull(endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation failure for end date existing while continued illness is true")
    @Test
    void shouldReturnStringIfEndDateExistsWhileContinuedIsTrue() {
        appeal.getReason().getIllness().setIllnessEnd(TestData.ILLNESS_END);
        appeal.getReason().getIllness().setContinuedIllness(true);
        assertEquals(EndDateValidator.END_DATE_CONTINUED_TRUE, endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation failure for end date after creation date")
    @Test
    void shouldReturnStringIfEndDateIsAfterCreationDate() {
        appeal.getReason().getIllness().setIllnessEnd("9999-01-01");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals(EndDateValidator.END_DATE_AFTER_CREATED, endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation failure for end date before start date")
    @Test
    void shouldReturnStringIfEndDateIsBeforeStartDate() {
        appeal.getReason().getIllness().setIllnessEnd("2020-01-01");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals(EndDateValidator.END_DATE_BEFORE_START_DATE, endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation failure for wrong date format")
    @Test
    void shouldReturnStringIfDateIsInWrongFormat() {
        appeal.getReason().getIllness().setIllnessEnd("01/JAN/2020");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals(EndDateValidator.WRONG_FORMAT, endDateValidator.validateEndDate(appeal));
    }

    @DisplayName("Validation success if end date is correct")
    @Test
    void shouldReturnNullIfEndDateIsCorrect() {
        appeal.getReason().getIllness().setIllnessEnd(TestData.ILLNESS_END);
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertNull(endDateValidator.validateEndDate(appeal));
    }

}
