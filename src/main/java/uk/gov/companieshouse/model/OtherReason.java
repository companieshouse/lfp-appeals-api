package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;

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
}
