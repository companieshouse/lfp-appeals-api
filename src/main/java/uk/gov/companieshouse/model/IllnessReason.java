package uk.gov.companieshouse.model;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;

@Valid
public class IllnessReason implements ReasonType {

    private final String illPerson;
    private final String otherPerson;
    private final String illnessStart;
    private final boolean continuedIllness;
    private final String illnessEnd;
    private final String illnessImpactFurtherInformation;
    private List<Attachment> attachments;

    public IllnessReason(String illPerson, String otherPerson, String illnessStart, boolean continuedIllness,
                     String illnessEnd, String illnessImpactFurtherInformation, List<Attachment> attachments) {
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

    public boolean getContinuedIllness() {
        return continuedIllness;
    }

    public String getIllnessEnd() {
        return illnessEnd;
    }

    public String getIllnessImpactFurtherInformation() {
        return illnessImpactFurtherInformation;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<Attachment> attachments) {
        if (attachments == null) {
            this.attachments = Collections.emptyList();
        }
        this.attachments = attachments;
    }

    @Override
    public String getReasonType() {
        return ReasonType.ILLNESS;
    }
}
