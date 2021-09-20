package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;

class EndDateValidatorTest
{

    private EndDateValidator endDateValidator;
    private Appeal appeal = new Appeal();

    @BeforeEach
    private void setup() {
        endDateValidator = new EndDateValidator();
        appeal = TestUtil.createAppeal(TestUtil.buildCreatedBy(),
            TestUtil.createReasonWithIllness());
        appeal.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldReturnStringIfEndDateIsEmpty(){
        appeal.getReason().getIllness().setIllnessEnd(null);
        appeal.getReason().getIllness().setContinuedIllness(false);
        assertEquals("Unable to validate. End date is empty",
            endDateValidator.validateEndDate(appeal));
    }

    @Test
    void shouldReturnStringIfEndDateExistsWhileContinuedIsTrue() {
        appeal.getReason().getIllness().setIllnessEnd(TestData.ILLNESS_END);
        appeal.getReason().getIllness().setContinuedIllness(true);
        assertEquals("Unable to validate. End Date can't exist if continued illness is true",
            endDateValidator.validateEndDate(appeal));
    }

    @Test
    void shouldReturnStringIfEndDateIsAfterCreationDate() {
        appeal.getReason().getIllness().setIllnessEnd("01/01/9999");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals("Unable to validate. End date is after date of creation",
            endDateValidator.validateEndDate(appeal));
    }

    @Test
    void shouldReturnStringIfEndDateIsBeforeStartDate() {
        appeal.getReason().getIllness().setIllnessEnd("01/01/2020");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals("Unable to validate. Illness End Date is before Start Date",
            endDateValidator.validateEndDate(appeal));
    }

    @Test
    void shouldReturnStringIfDateIsInWrongFormat() {
        appeal.getReason().getIllness().setIllnessEnd("01/JAN/2020");
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertEquals("Unable to Parse Date. Wrong format",
            endDateValidator.validateEndDate(appeal));
    }

    @Test
    void shouldReturnNullIfEndDateIsCorrect() {
        appeal.getReason().getIllness().setIllnessEnd(TestData.ILLNESS_END);
        appeal.getReason().getIllness().setContinuedIllness(false);
        appeal.getReason().getIllness().setIllnessStart(TestData.ILLNESS_START);
        assertNull(endDateValidator.validateEndDate(appeal));
    }

}
