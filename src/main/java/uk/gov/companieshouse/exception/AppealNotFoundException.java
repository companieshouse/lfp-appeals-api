package uk.gov.companieshouse.exception;

public class AppealNotFoundException extends RuntimeException {

    public AppealNotFoundException(String id) {
        super("Appeal not found for id: " + id);
    }

}
