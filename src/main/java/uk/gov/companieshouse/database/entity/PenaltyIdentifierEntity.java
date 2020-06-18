package uk.gov.companieshouse.database.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;

import java.io.Serializable;

@AccessType(AccessType.Type.PROPERTY)
public class PenaltyIdentifierEntity implements Serializable {

    private final String companyNumber;
    private final String penaltyReference;

    public PenaltyIdentifierEntity(String companyNumber, String penaltyReference) {
        this.companyNumber = companyNumber;
        this.penaltyReference = penaltyReference;
    }

    public String getCompanyNumber() {
        return this.companyNumber;
    }

    public String getPenaltyReference() {
        return this.penaltyReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PenaltyIdentifierEntity that = (PenaltyIdentifierEntity) o;

        return new EqualsBuilder()
            .append(companyNumber, that.companyNumber)
            .append(penaltyReference, that.penaltyReference)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(companyNumber)
            .append(penaltyReference)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("companyNumber", companyNumber)
            .append("penaltyReference", penaltyReference)
            .toString();
    }
}
