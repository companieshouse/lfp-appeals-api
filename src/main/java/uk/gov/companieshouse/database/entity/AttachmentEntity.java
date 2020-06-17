package uk.gov.companieshouse.database.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;

import java.io.Serializable;

@AccessType(AccessType.Type.PROPERTY)
public class AttachmentEntity implements Serializable {

    private final String id;
    private final String name;
    private final String contentType;
    private final Integer size;

    public AttachmentEntity(String id, String name, String contentType, Integer size) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttachmentEntity that = (AttachmentEntity) o;

        return new EqualsBuilder()
            .append(id, that.id)
            .append(name, that.name)
            .append(contentType, that.contentType)
            .append(size, that.size)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(id)
            .append(name)
            .append(contentType)
            .append(size)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("contentType", contentType)
            .append("size", size)
            .toString();
    }
}
