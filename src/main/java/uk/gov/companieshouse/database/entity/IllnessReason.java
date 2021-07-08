package uk.gov.companieshouse.database.entity;

import org.springframework.data.annotation.AccessType;

import java.io.Serializable;
import java.util.List;

@AccessType(AccessType.Type.PROPERTY)
public class IllnessReason implements Serializable {

    private final String illPerson;
    private final String otherPerson;
    private final String illnessStart;
    private final String continuedIllness;
    private final String illnessEnd;
    private final String illnessImpactFurtherInformation;
    private final List<AttachmentEntity> attachments;

    public IllnessReason(String illPerson, String otherPerson, String illnessStart, String continuedIllness,
                     String illnessEnd, String illnessImpactFurtherInformation, List<AttachmentEntity> attachments) {
        this.illPerson = illPerson;
        this.otherPerson = otherPerson;
        this.illnessStart = illnessStart;
        this.continuedIllness = continuedIllness;
        this.illnessEnd = illnessEnd;
        this.illnessImpactFurtherInformation = illnessImpactFurtherInformation;
        this.attachments = attachments;
    }

    public String getIllPerson() {
        return illPerson;
    }

    public String getOtherPerson() {
        return otherPerson;
    }

    public String getIllnessStart() {
        return illnessStart;
    }

    public String getContinuedIllness() {
        return continuedIllness;
    }

    public String getIllnessEnd() {
        return illnessEnd;
    }

    public String getIllnessImpactFurtherInformation() {
        return illnessImpactFurtherInformation;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }
}
