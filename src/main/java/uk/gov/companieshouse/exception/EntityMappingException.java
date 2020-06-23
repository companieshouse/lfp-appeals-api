package uk.gov.companieshouse.exception;

public class EntityMappingException extends IllegalArgumentException {

    public EntityMappingException(String reason) {
        super(reason);
    }
    
}