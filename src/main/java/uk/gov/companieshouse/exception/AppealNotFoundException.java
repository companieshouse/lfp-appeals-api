package uk.gov.companieshouse.exception;

public class AppealNotFoundException extends RuntimeException {

    public AppealNotFoundException(Long id) {
        super("Appeal not found for id: " + id);
    }

}
