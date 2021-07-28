package uk.gov.companieshouse.model;

import javax.validation.Valid;

public class Reason {

    @Valid
    private OtherReason other;
    @Valid
    private IllnessReason illnessReason;

    public OtherReason getOther() {
        return this.other;
    }

    public void setOther(OtherReason other) {
        this.other = other;
    }

    public IllnessReason getIllnessReason() {
        return this.illnessReason;
    }

    public void setIllnessReason(IllnessReason illnessReason) {
        this.illnessReason = illnessReason;
    }

    @Override
    public String toString() {
        return "Reason{" +
            "other=" + other +
            "illnessReason=" + illnessReason +
            '}';
    }
}
