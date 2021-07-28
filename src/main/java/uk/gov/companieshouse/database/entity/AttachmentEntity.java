package uk.gov.companieshouse.database.entity;

import java.io.Serializable;
import org.springframework.data.annotation.AccessType;

@AccessType(AccessType.Type.PROPERTY)
public class AttachmentEntity implements Serializable {

    private String id;
    private String name;
    private String contentType;
    private Integer size;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getSize() {
        return size;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }
}
