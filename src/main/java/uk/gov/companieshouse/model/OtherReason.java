package uk.gov.companieshouse.model;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class OtherReason {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "description must not be blank")
    private String description;

    @Valid
    private List<Attachment> attachments;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setAttachments(final List<Attachment> attachments) {
        if (attachments == null) {
            this.attachments = Collections.emptyList();
        }
        this.attachments = attachments;
    }

}
