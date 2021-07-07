package uk.gov.companieshouse.database.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.AccessType;

@AccessType(AccessType.Type.PROPERTY)
public class IllnessReasonEntity implements Serializable {

    private final String illPerson;
    private final String otherPerson;
    private final String illnessStartDate;
    private final Boolean continuedIllness;
    private final String illnessEndDate;
    private final String illnessImpactFurtherInformation;
    private final List<AttachmentEntity> attachments;

    public IllnessReasonEntity(String illPerson, String otherPerson, String illnessStartDate, Boolean continuedIllness,
        String illnessEndDate, String illnessImpactFurtherInformation, List<AttachmentEntity> attachments) {
        this.illPerson = illPerson;
        this.otherPerson = otherPerson;
        this.illnessStartDate = illnessStartDate;
        this.continuedIllness = continuedIllness;
        this.illnessEndDate = illnessEndDate;
        this.illnessImpactFurtherInformation = illnessImpactFurtherInformation;
        this.attachments = attachments;
    }

    public String getIllPerson() {
        return this.illPerson;
    }

    public String getOtherPerson() {
        return otherPerson;
    }

    public String getIllnessStartDate() {
        return this.illnessStartDate;
    }

    public Boolean getContinuedIllness() {
        return continuedIllness;
    }

    public String getIllnessEndDate() {
        return illnessEndDate;
    }

    public String getIllnessImpactFurtherInformation() {
        return illnessImpactFurtherInformation;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IllnessReasonEntity that = (IllnessReasonEntity) o;
        return Objects.equals(illPerson, that.illPerson)
            && Objects.equals(illnessStartDate, that.illnessStartDate)
            && Objects.equals(continuedIllness, that.continuedIllness)
            && Objects.equals(illnessEndDate, that.illnessEndDate)
            && Objects.equals(illnessImpactFurtherInformation, that.illnessImpactFurtherInformation)
            && Objects.equals(attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(illPerson, otherPerson, illnessStartDate, continuedIllness, illnessEndDate,
            illnessImpactFurtherInformation,
            attachments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("illPerson", illPerson)
            .append("otherPerson", otherPerson)
            .append("illnessStartDate", illnessStartDate)
            .append("continuedIllness", continuedIllness)
            .append("illnessEndDate", illnessEndDate)
            .append("illnessImpactFurtherInformation", illnessImpactFurtherInformation)
            .append("attachments", attachments)
            .toString();
    }
}
