package uk.gov.companieshouse.model.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Appeal;

@Component
public class EndDateValidator {

    static final String EMPTY_END_DATE = "Unable to validate. End date is empty";
    static final String END_DATE_CONTINUED_TRUE =
        "Unable to validate. End Date can't exist if continued illness is true";
    static final String END_DATE_AFTER_CREATED = "Unable to validate. End date is after date of creation";
    static final String END_DATE_BEFORE_START_DATE = "Unable to validate. Illness End Date is before Start Date";
    static final String WRONG_FORMAT = "Unable to Parse Date. Wrong format";

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK);

    public String validateEndDate(Appeal appeal) {
        String rawStartDate = appeal.getReason().getIllness().getIllnessStart();
        String rawEndDate = appeal.getReason().getIllness().getIllnessEnd();
        boolean continued = appeal.getReason().getIllness().getContinuedIllness();

        if ((rawEndDate == null || rawEndDate.isEmpty()) && !continued) {
            return EMPTY_END_DATE;
        }

        if (rawEndDate != null && !rawEndDate.isEmpty() && continued) {
            return END_DATE_CONTINUED_TRUE;
        }

        if (rawEndDate != null && !rawEndDate.isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(rawEndDate, dtf);
                LocalDate startDate = LocalDate.parse(rawStartDate, dtf);

                if (endDate.isBefore(startDate)) {
                    return END_DATE_BEFORE_START_DATE;
                }
                else if (endDate.isAfter(LocalDate.now())) {
                    return END_DATE_AFTER_CREATED;
                }
            }
            catch (DateTimeParseException dte) {
                return WRONG_FORMAT;
            }
        }
        return null;
    }
}

