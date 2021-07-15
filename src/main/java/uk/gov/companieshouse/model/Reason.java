package uk.gov.companieshouse.model;

import java.util.Objects;
import javax.validation.Valid;

public class Reason {

    @Valid
    private OtherReason other;
    @Valid
    private IllnessReason illnessReason;

    public Reason() {
        this(null, null);
    }

    public Reason(OtherReason other, IllnessReason illnessReason) {
        this.other = other;
        this.illnessReason = illnessReason;

    }

    public OtherReason getOther() {
        return this.other;
    }

    public void setOther(OtherReason other) {
        this.other = other;
    }

    public IllnessReason getIllnessReason() { return this.illnessReason; }

    public void setIllnessReason(IllnessReason illnessReason) {this.illnessReason = illnessReason; }

    public ReasonType getReasonType() {
        if(getOther() == null ^ getIllnessReason() == null){
            return getOther() == null? getIllnessReason():getOther();
        }
        else {
            return null;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Reason reason = (Reason) o;
        return Objects.equals(other, reason.other) && Objects.equals(illnessReason, reason.illnessReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(other, illnessReason);
    }

    @Override
    public String toString() {
        return "Reason{" +
            "other=" + other +
            "illnessReason=" + illnessReason +
            '}';
    }
}
