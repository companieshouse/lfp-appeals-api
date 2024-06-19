package uk.gov.companieshouse.model;

import jakarta.validation.Valid;

public class Reason {

    @Valid
    private OtherReason other;
    @Valid
    private IllnessReason illness;

    public OtherReason getOther() {
        return this.other;
    }

    public void setOther(OtherReason other) {
        this.other = other;
    }

    public IllnessReason getIllness() {
        return this.illness;
    }

    public void setIllness(IllnessReason illnessReason) {
        this.illness = illnessReason;
    }

    @Override
    public String toString() {
        return "Reason{" +
            "other=" + other +
            "illnessReason=" + illness +
            '}';
    }
}
