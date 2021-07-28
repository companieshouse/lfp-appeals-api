package uk.gov.companieshouse.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Valid
public class IllnessReason {

    @NotBlank(message = "illPerson must not be blank")
    private String illPerson;

    @NotBlank(message = "illnessStart must not be blank")
    private String illnessStart;

    @NotNull(message = "continuedIllness must not be null")
    private boolean continuedIllness;

    @NotBlank(message = "illnessImpactFurtherInformation must not be blank")
    private String illnessImpactFurtherInformation;

    private String otherPerson;
    private String illnessEnd;

    @Valid
    private List<Attachment> attachments;

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

    public void setIllPerson(final String illPerson) {
        this.illPerson = illPerson;
    }

    public void setIllnessStart(final String illnessStart) {
        this.illnessStart = illnessStart;
    }

    public void setContinuedIllness(final boolean continuedIllness) {
        this.continuedIllness = continuedIllness;
    }

    public void setIllnessImpactFurtherInformation(final String illnessImpactFurtherInformation) {
        this.illnessImpactFurtherInformation = illnessImpactFurtherInformation;
    }

    public void setOtherPerson(final String otherPerson) {
        this.otherPerson = otherPerson;
    }

    public void setIllnessEnd(final String illnessEnd) {
        this.illnessEnd = illnessEnd;
    }

    public void setAttachments(final List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
