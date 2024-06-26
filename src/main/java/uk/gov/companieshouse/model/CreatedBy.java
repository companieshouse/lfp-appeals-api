package uk.gov.companieshouse.model;

import jakarta.validation.constraints.NotBlank;

public class CreatedBy {

    @NotBlank(message = "createdBy.id must not be blank")
    private String id;

    @NotBlank(message = "createdBy.name must not be blank")
    private String name;

    private String relationshipToCompany;

    @NotBlank(message = "createdBy.emailAddress must not be blank")
    private String emailAddress;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRelationshipToCompany() {
        return relationshipToCompany;
    }

    public void setRelationshipToCompany(final String relationshipToCompany) {
        this.relationshipToCompany = relationshipToCompany;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
