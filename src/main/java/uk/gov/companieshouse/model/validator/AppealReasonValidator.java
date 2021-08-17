package uk.gov.companieshouse.model.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Reason;


@Component
public class AppealReasonValidator {

    /**
     * Validate that exactly one appeal reason has been supplied.
     * @param reason reason for the appeal
     * @return String details of the validation error
     */
    public String validate(Reason reason) {

        if (reason.getOther() != null ^ reason.getIllness() != null) {
            return null;
        }

        if (reason.getOther() == null && reason.getIllness() == null) {
            return "Other/Illness must be supplied with an Appeal";
        }

        return "Only one reason type can be supplied with an Appeal";
    }
}
