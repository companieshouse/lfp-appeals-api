package uk.gov.companieshouse.exception;

public class AppealReasonException extends RuntimeException {
    public AppealReasonException(String message) {
        super(message);
    }

    public AppealReasonException(String message, Throwable cause) {
        super(message, cause);
    }
}
