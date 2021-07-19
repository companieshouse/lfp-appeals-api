package uk.gov.companieshouse.model;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
public class IllnessReason extends ReasonType {

    @NotBlank(message = "illPerson must not be blank")
    private final String illPerson;

    @NotBlank(message = "illnessStart must not be blank")
    private final String illnessStart;

    @NotNull(message = "continuedIllness must not be null")
    private final boolean continuedIllness;

    @NotBlank(message = "illnessImpactFurtherInformation must not be blank")
    private final String illnessImpactFurtherInformation;

    private final String otherPerson;
    private final String illnessEnd;

    @Valid
    private List<Attachment> attachments;

    public IllnessReason(){
        this(null,null,null,false,null,null,Collections.emptyList());
    }

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
