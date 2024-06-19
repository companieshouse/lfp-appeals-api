package uk.gov.companieshouse.exception;

public class JsonException extends RuntimeException{

    private static final long serialVersionUID = 0;

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
