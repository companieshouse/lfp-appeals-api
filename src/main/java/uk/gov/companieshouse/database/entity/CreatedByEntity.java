package uk.gov.companieshouse.database.entity;

import java.io.Serializable;
import org.springframework.data.annotation.AccessType;

@AccessType(AccessType.Type.PROPERTY)
public class CreatedByEntity implements Serializable {

    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
