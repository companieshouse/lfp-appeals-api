package uk.gov.companieshouse.model.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Appeal;

@Component
public class EndDateValidator {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK);

    public String validateEndDate(Appeal appeal) {
        String rawStartDate = appeal.getReason().getIllness().getIllnessStart();
        String rawEndDate = appeal.getReason().getIllness().getIllnessEnd();

        if (!appeal.getReason().getIllness().getContinuedIllness()
            && appeal.getReason().getIllness().getIllnessEnd() == null) {
            return "Unable to validate. End date is empty";
        }
        if (!appeal.getReason().getIllness().getIllnessEnd().isEmpty() && appeal.getReason().getIllness()
            .getContinuedIllness()) {
            return "Unable to validate. End Date can't exist if continued illness is true";
        }

        try {
            LocalDate endDate = LocalDate.parse(rawEndDate, dtf);
            LocalDate startDate = LocalDate.parse(rawStartDate, dtf);

            if (endDate.isEqual(startDate) || endDate.isAfter(startDate)) {
                if (endDate.isAfter(appeal.getCreatedAt().toLocalDate())) {
                    return "Unable to validate. End date is after date of creation";
                }
                return null;
            }
            if (endDate.isBefore(startDate)) {
                return "Unable to validate. Illness End Date is before Start Date";
            }

        }
        catch (DateTimeParseException dte) {
            return "Unable to Parse Date. Wrong format";
        }

        return null;
    }
}
