package uk.gov.companieshouse.model.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.Appeal;

@Component
public class EndDateValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);

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
        Boolean continued = appeal.getReason().getIllness().getContinuedIllness();

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("rawStartDate", rawEndDate);
        logMap.put("rawEndDate", rawEndDate);
        logMap.put("continuedIllness", continued.toString());

        LOGGER.debugContext(appeal.getPenaltyIdentifier().getPenaltyReference(), "Determine if illness end date is valid", logMap);
        
        if ((rawEndDate == null || rawEndDate.isEmpty()) && !continued.booleanValue()) {
            return EMPTY_END_DATE;
        }

        if (rawEndDate != null && !rawEndDate.isEmpty() && continued.booleanValue()) {
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

