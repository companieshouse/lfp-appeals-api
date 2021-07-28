package uk.gov.companieshouse.util;

import java.util.List;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;

public class TestUtil {
    public static IllnessReason createIllnessReason() {
        IllnessReason illnessReason = new IllnessReason();
        illnessReason.setIllPerson(TestData.ILL_PERSON);
        illnessReason.setOtherPerson(TestData.OTHER_PERSON);
        illnessReason.setIllnessStart(TestData.ILLNESS_START);
        illnessReason.setContinuedIllness(TestData.CONTINUED_ILLNESS);
        illnessReason.setIllnessEnd(TestData.ILLNESS_END);
        illnessReason.setIllnessImpactFurtherInformation(TestData.ILLNESS_IMPACT_FURTHER_INFORMATION);


        Attachment attachment = createAttachment();

        illnessReason.setAttachments(List.of(attachment));

        return illnessReason;
    }

    public static Attachment createAttachment() {
        Attachment attachment = new Attachment();
        attachment.setId(TestData.ATTACHMENT_ID);
        attachment.setName(TestData.ATTACHMENT_NAME);
        attachment.setContentType(TestData.CONTENT_TYPE);
        attachment.setSize(TestData.ATTACHMENT_SIZE);
        attachment.setUrl(TestData.ATTACHMENT_URL);
        return attachment;
    }

    public static IllnessReasonEntity createIllnessReasonEntity(){
        IllnessReasonEntity illnessReasonEntity = new IllnessReasonEntity();
        illnessReasonEntity.setIllPerson(TestData.ILL_PERSON);
        illnessReasonEntity.setOtherPerson(TestData.OTHER_PERSON);
        illnessReasonEntity.setIllnessStartDate(TestData.ILLNESS_START);
        illnessReasonEntity.setContinuedIllness(TestData.CONTINUED_ILLNESS);
        illnessReasonEntity.setIllnessEndDate(TestData.ILLNESS_END);
        illnessReasonEntity.setIllnessImpactFurtherInformation(TestData.ILLNESS_IMPACT_FURTHER_INFORMATION);

        AttachmentEntity attachmentEntity = createAttachmentEntity();

        illnessReasonEntity.setAttachments(List.of(attachmentEntity));

        return illnessReasonEntity;

    }

    public static OtherReason createOtherReason() {
        OtherReason otherReason = new OtherReason();
        otherReason.setTitle(TestData.TITLE);
        otherReason.setDescription(TestData.DESCRIPTION);

        Attachment attachment = createAttachment();

        otherReason.setAttachments(List.of(attachment));

        return otherReason;
    }

    public static OtherReasonEntity createOtherReasonEntity() {
        OtherReasonEntity otherReasonEntity = new OtherReasonEntity();
        otherReasonEntity.setTitle(TestData.TITLE);
        otherReasonEntity.setDescription(TestData.DESCRIPTION);

        AttachmentEntity attachment = createAttachmentEntity();

        otherReasonEntity.setAttachments(List.of(attachment));

        return otherReasonEntity;
    }

    public static AttachmentEntity createAttachmentEntity() {
        AttachmentEntity attachment = new AttachmentEntity();
        attachment.setId(TestData.ATTACHMENT_ID);
        attachment.setName(TestData.ATTACHMENT_NAME);
        attachment.setContentType(TestData.CONTENT_TYPE);
        attachment.setSize(TestData.ATTACHMENT_SIZE);
        return attachment;
    }


}
