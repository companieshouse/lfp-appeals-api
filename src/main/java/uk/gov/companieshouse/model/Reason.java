package uk.gov.companieshouse.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Reason {

    @Valid
    @NotNull(message = "other must not be null")
    private OtherReason other;

    public Reason() {
    }

    public OtherReason getOther() {
        return this.other;
    }

    public void setOther(OtherReason other) {
        this.other = other;
    }
}
