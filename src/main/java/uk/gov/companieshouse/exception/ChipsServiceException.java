package uk.gov.companieshouse.exception;

public class ChipsServiceException extends RuntimeException {
    public ChipsServiceException(String errorMessage) {
        super(errorMessage);
    }

    public ChipsServiceException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
