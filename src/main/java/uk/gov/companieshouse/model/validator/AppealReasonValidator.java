package uk.gov.companieshouse.model.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.exception.AppealReasonException;
import uk.gov.companieshouse.model.Reason;

@Component
public class AppealReasonValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealReasonValidator.class);

    public void validate(Reason reason) throws AppealReasonException {
        if(reason.getOther() == null ^ reason.getIllnessReason() == null){
            LOGGER.info("Correctly posted {} appeal reason", reason.getReasonType());
        }
        else {
            throw new AppealReasonException("Invalid appeal reason");
        }
    }
}
