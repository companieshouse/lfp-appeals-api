package uk.gov.companieshouse.database.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "appeals")
@AccessType(AccessType.Type.PROPERTY)
public class AppealEntity implements Serializable {

    @Id
    @SuppressWarnings("FieldMayBeFinal") // Non final field is required by Spring Data
    private String id;
    private final LocalDateTime createdAt;
    private final CreatedByEntity createdBy;
    private final PenaltyIdentifierEntity penaltyIdentifier;
    private final ReasonEntity reason;

    public AppealEntity(String id, LocalDateTime createdAt, CreatedByEntity createdBy, PenaltyIdentifierEntity penaltyIdentifier, ReasonEntity reason) {
        this.id = id;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.penaltyIdentifier = penaltyIdentifier;
        this.reason = reason;
    }

    public String getId() {
        return this.id;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public CreatedByEntity getCreatedBy() {
        return this.createdBy;
    }

    public PenaltyIdentifierEntity getPenaltyIdentifier() {
        return this.penaltyIdentifier;
    }

    public ReasonEntity getReason() {
        return this.reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AppealEntity that = (AppealEntity) o;

        return new EqualsBuilder()
            .append(id, that.id)
            .append(createdAt, that.createdAt)
            .append(createdBy, that.createdBy)
            .append(penaltyIdentifier, that.penaltyIdentifier)
            .append(reason, that.reason)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(id)
            .append(createdAt)
            .append(createdBy)
            .append(penaltyIdentifier)
            .append(reason)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("createdAt", createdAt)
            .append("createdBy", createdBy)
            .append("penaltyIdentifier", penaltyIdentifier)
            .append("reason", reason)
            .toString();
    }
}
