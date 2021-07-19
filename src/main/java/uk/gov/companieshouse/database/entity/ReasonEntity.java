package uk.gov.companieshouse.database.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;

import java.io.Serializable;

@AccessType(AccessType.Type.PROPERTY)
public class ReasonEntity implements Serializable {

    private final OtherReasonEntity other;
    private final IllnessReasonEntity illnessReason;

    public ReasonEntity(OtherReasonEntity other, IllnessReasonEntity illnessReason) {
        this.other = other;
        this.illnessReason = illnessReason;
    }

    public OtherReasonEntity getOther() {
        return this.other;
    }

    public IllnessReasonEntity getIllnessReason() {
        return this.illnessReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReasonEntity that = (ReasonEntity) o;

        return new EqualsBuilder()
            .append(other, that.other)
            .append(illnessReason, that.illnessReason)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(other)
            .append(illnessReason)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("other", other)
            .append("illnessReason", illnessReason)
            .toString();
    }
}
