package uk.gov.companieshouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ChipsServiceException extends RuntimeException {
    public ChipsServiceException(String errorMessage) {
        super(errorMessage);
    }
}
