package uk.gov.companieshouse.model;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OtherReason implements ReasonType {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "description must not be blank")
    private String description;

    @Valid
    private List<Attachment> attachments;

    public OtherReason() {
        this(null, null, Collections.emptyList());
    }

    public OtherReason(String title, String description, List<Attachment> attachments) {
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

    @Override
    public String getReasonType() {
        return ReasonType.OTHER;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OtherReason that = (OtherReason) o;
        return Objects.equals(title, that.title) &&
               Objects.equals(description, that.description) && 
               attachments.equals(that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, attachments);
    }

    @Override
    public String toString() {

        final String attachmentsAsString = attachments.stream()
            .reduce("", (a, b) -> a + ", [" + b.toString() + "]", String::concat);

        return "OtherReason{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", attachments='" + attachmentsAsString + '\'' +
            '}';
    }
}
