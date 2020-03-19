package uk.gov.companieshouse.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
}
