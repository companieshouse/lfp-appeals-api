package uk.gov.companieshouse.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Reason;

@Component
public class AppealReasonValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealReasonValidator.class);

    public boolean isValid(Reason reason) {
        return (reason.getReasonType() != null) ? true : false;
    }
}
