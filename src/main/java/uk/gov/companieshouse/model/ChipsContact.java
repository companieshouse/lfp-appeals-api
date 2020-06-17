package uk.gov.companieshouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ChipsContact {

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("contact_description")
    private String contactDescription;

    @JsonProperty("date_received")
    private String dateReceived;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getContactDescription() {
        return contactDescription;
    }

    public void setContactDescription(String contactDescription) {
        this.contactDescription = contactDescription;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChipsContact that = (ChipsContact) o;
        return Objects.equals(companyNumber, that.companyNumber) &&
            Objects.equals(contactDescription, that.contactDescription) &&
            Objects.equals(dateReceived, that.dateReceived);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber, contactDescription, dateReceived);
    }

    @Override
    public String toString() {
        return "ChipsContact{" +
            "companyNumber='" + companyNumber + '\'' +
            ", contactDescription='" + contactDescription + '\'' +
            ", dateReceived='" + dateReceived + '\'' +
            '}';
    }
}
