package uk.gov.companieshouse.database.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;

import java.io.Serializable;
import java.util.List;

@AccessType(AccessType.Type.PROPERTY)
public class OtherReasonEntity implements Serializable {

    private final String title;
    private final String description;
    private final List<AttachmentEntity> attachments;

    public OtherReasonEntity(String title, String description, List<AttachmentEntity> attachments) {
        this.title = title;
        this.description = description;
        this.attachments = attachments;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OtherReasonEntity that = (OtherReasonEntity) o;

        return new EqualsBuilder()
            .append(title, that.title)
            .append(description, that.description)
            .append(attachments, that.attachments)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(title)
            .append(description)
            .append(attachments)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("title", title)
            .append("description", description)
            .append("attachments", attachments)
            .toString();
    }
}
