package uk.gov.companieshouse.database.entity;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.annotation.AccessType;

@AccessType(AccessType.Type.PROPERTY)
public class OtherReasonEntity implements Serializable {

    private String title;
    private String description;
    private List<AttachmentEntity> attachments;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setAttachments(final List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }
}
