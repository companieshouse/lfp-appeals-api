package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import uk.gov.companieshouse.model.validator.ValidCompanyNumber;

import java.util.Objects;

public class PenaltyIdentifier {

    @ValidCompanyNumber
    private String companyNumber;

    @NotBlank(message = "penaltyReference must not be blank")
    @Pattern(regexp = "([A-Z0-9/]{8,14})", //^[A-Z][0-9]{8}$|^PEN\s?[1-2]A\/[0-9]{8}$
        message = "Unable to Validate. penaltyReference is invalid")
    private String penaltyReference;

    public PenaltyIdentifier() {
        this(null, null);
    }

    public PenaltyIdentifier(String companyNumber, String penaltyReference) {
        this.companyNumber = companyNumber;
        this.penaltyReference = penaltyReference;
    }

    public String getCompanyNumber() {
        return this.companyNumber;
    }

    public String getPenaltyReference() {
        return this.penaltyReference;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setPenaltyReference(String penaltyReference) {
        this.penaltyReference = penaltyReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PenaltyIdentifier that = (PenaltyIdentifier) o;
        return Objects.equals(companyNumber, that.companyNumber) &&
            Objects.equals(penaltyReference, that.penaltyReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber, penaltyReference);
    }

    @Override
    public String toString() {
        return "PenaltyIdentifier{" +
            "companyNumber='" + companyNumber + '\'' +
            ", penaltyReference='" + penaltyReference + '\'' +
            '}';
    }
}
