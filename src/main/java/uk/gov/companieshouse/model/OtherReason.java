package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class OtherReason {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "description must not be blank")
    private String description;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherReason that = (OtherReason) o;
        return Objects.equals(title, that.title) &&
            Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description);
    }

    @Override
    public String toString() {
        return "OtherReason{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
