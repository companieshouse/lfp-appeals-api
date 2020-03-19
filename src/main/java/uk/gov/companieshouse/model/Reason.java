package uk.gov.companieshouse.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Reason {

    @Valid
    @NotNull(message = "other must not be null")
    private OtherReason other;

    public OtherReason getOther() {
        return this.other;
    }

    public void setOther(OtherReason other) {
        this.other = other;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason reason = (Reason) o;
        return Objects.equals(other, reason.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(other);
    }

    @Override
    public String toString() {
        return "Reason{" +
            "other=" + other +
            '}';
    }
}
