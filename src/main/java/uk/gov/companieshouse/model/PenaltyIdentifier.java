package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class PenaltyIdentifier {

    @NotBlank(message = "companyNumber must not be blank")
    @Pattern(regexp = "(((SC|NI)[0-9]{1,6})|([0-9]{1,8}))",
        message = "companyNumber is invalid")
    private String companyNumber;

    @NotBlank(message = "penaltyReference must not be blank")
    @Pattern(regexp = "([A-Z]{1}[0-9]{8})",
        message = "penaltyReference is invalid")
    private String penaltyReference;

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
