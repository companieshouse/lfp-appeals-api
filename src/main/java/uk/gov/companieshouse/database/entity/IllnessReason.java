package uk.gov.companieshouse.database.entity;

import org.springframework.data.annotation.AccessType;

import java.util.List;

@AccessType(AccessType.Type.PROPERTY)
public class IllnessReason {

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


}
