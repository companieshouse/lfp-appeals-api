package uk.gov.companieshouse.model.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Reason;

@Component
public class AppealReasonValidator {
    public boolean isValid(Reason reason) {
        return reason.getReasonType() != null;
    }
}
