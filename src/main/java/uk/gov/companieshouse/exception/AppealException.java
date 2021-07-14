package uk.gov.companieshouse.exception;

public class AppealException extends RuntimeException {

    public AppealException(final String message) {
        super(message);
    }

    public AppealException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
