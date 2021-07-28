package uk.gov.companieshouse.database.entity;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.annotation.AccessType;

@AccessType(AccessType.Type.PROPERTY)
public class IllnessReasonEntity implements Serializable {

    private String illPerson;
    private String otherPerson;
    private String illnessStartDate;
    private Boolean continuedIllness;
    private String illnessEndDate;
    private String illnessImpactFurtherInformation;
    private List<AttachmentEntity> attachments;

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

    public void setIllPerson(final String illPerson) {
        this.illPerson = illPerson;
    }

    public void setOtherPerson(final String otherPerson) {
        this.otherPerson = otherPerson;
    }

    public void setIllnessStartDate(final String illnessStartDate) {
        this.illnessStartDate = illnessStartDate;
    }

    public void setContinuedIllness(final Boolean continuedIllness) {
        this.continuedIllness = continuedIllness;
    }

    public void setIllnessEndDate(final String illnessEndDate) {
        this.illnessEndDate = illnessEndDate;
    }

    public void setIllnessImpactFurtherInformation(final String illnessImpactFurtherInformation) {
        this.illnessImpactFurtherInformation = illnessImpactFurtherInformation;
    }

    public void setAttachments(final List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }
}
