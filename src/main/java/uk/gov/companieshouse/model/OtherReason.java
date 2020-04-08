package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public class OtherReason {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "description must not be blank")
    private String description;

    @NotNull()
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherReason that = (OtherReason) o;
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

        String attachmentsAsString = attachments.stream()
            .reduce("", (a, b) -> a + ", [" + b.toString() + "]", String::concat);

        return "OtherReason{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", attachements='" + attachmentsAsString + '\'' +
            '}';
    }
}
