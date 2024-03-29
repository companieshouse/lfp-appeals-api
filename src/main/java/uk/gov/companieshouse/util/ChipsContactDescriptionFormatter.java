package uk.gov.companieshouse.util;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class ChipsContactDescriptionFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String FURTHER_INFORMATION = "\nFurther information: ";
    private static final String SUPPORTING_DOCUMENTS = "\nSupporting documents: ";

    public ChipsContact buildChipsContact(Appeal appeal) {
        String companyNumber = appeal.getPenaltyIdentifier().getCompanyNumber();
        OtherReason otherReason = appeal.getReason().getOther();
        IllnessReason illnessReason = appeal.getReason().getIllness();

        ChipsContact chipsContact = new ChipsContact();
        chipsContact.setCompanyNumber(companyNumber);
        chipsContact.setDateReceived(appeal.getCreatedAt().format(DATE_TIME_FORMATTER));

        StringBuilder contactDescription = new StringBuilder();


        if(otherReason != null){
            formatAppealWithRelationshipDescription(contactDescription, companyNumber, appeal.getCreatedBy().getName(),
                appeal.getCreatedBy().getRelationshipToCompany(), appeal.getCreatedBy().getEmailAddress());
            formatOtherReasonContactDescription(contactDescription, otherReason, appeal.getId());
        }

        if(illnessReason != null){
            formatAppealDescription(contactDescription, companyNumber, appeal.getCreatedBy().getName(), appeal.getCreatedBy().getEmailAddress());
            if(illnessReason.getContinuedIllness()) {
                formatIllnessReasonContacDescriptionContinuedIllness(contactDescription, illnessReason, appeal.getId());
            }
            else formatIllnessReasonContactDescriptionIllnessEnd(contactDescription, illnessReason, appeal.getId());
        }

        chipsContact.setContactDescription(contactDescription.toString());
        return chipsContact;
    }

    private void formatAppealWithRelationshipDescription(StringBuilder contactDescription, String companyNumber, String yourName, String relationship, String emailAddress){
        contactDescription.append("Appeal submitted" + "\n\nYour reference number is your company number " + companyNumber);
        contactDescription.append("\n\nCompany Number: " + companyNumber + "\nName of User: " + yourName +
            "\nRelationship to Company: " + relationship + "\nEmail address: " + emailAddress + "\n\nAppeal Reason");
    }

    private void formatAppealDescription(StringBuilder contactDescription, String companyNumber, String yourName, String emailAddress){
        contactDescription.append("Appeal submitted" + "\n\nYour reference number is your company number " + companyNumber);
        contactDescription.append("\n\nCompany Number: " + companyNumber + "\nName of User: " + yourName +"\nEmail address: "
            + emailAddress + "\n\nAppeal Reason");
    }

    private void formatOtherReasonContactDescription(StringBuilder contactDescription, OtherReason otherReason, String appealId){
        List<Attachment> attachmentList = otherReason.getAttachments();
        contactDescription.append("\nReason: " + otherReason.getTitle() + FURTHER_INFORMATION + otherReason.getDescription());
        contactDescription.append(SUPPORTING_DOCUMENTS + getAttachmentsStr(appealId, attachmentList));
    }

    private void formatIllnessReasonContactDescriptionIllnessEnd(StringBuilder contactDescription, IllnessReason illnessReason, String appealId){
        List<Attachment> attachmentList = illnessReason.getAttachments();
        contactDescription.append(
            "\nIll Person: " + illnessReason.getIllPerson() +
            "\nOther Person: " + illnessReason.getOtherPerson() +
            "\nIllness Start Date: " + illnessReason.getIllnessStart() +
            "\nContinued Illness: " + illnessReason.getContinuedIllness() +
            "\nIllness End Date: " + illnessReason.getIllnessEnd() +
            FURTHER_INFORMATION + illnessReason.getIllnessImpactFurtherInformation()
        );
        contactDescription.append(SUPPORTING_DOCUMENTS + getAttachmentsStr(appealId, attachmentList));
    }

    private void formatIllnessReasonContacDescriptionContinuedIllness(StringBuilder contactDescription, IllnessReason illnessReason, String appealId){
        List<Attachment> attachmentList = illnessReason.getAttachments();
        contactDescription.append(
            "\nIll Person: " + illnessReason.getIllPerson() +
                "\nOther Person: " + illnessReason.getOtherPerson() +
                "\nIllness Start Date: " + illnessReason.getIllnessStart() +
                "\nContinued Illness: " + illnessReason.getContinuedIllness() +
                FURTHER_INFORMATION + illnessReason.getIllnessImpactFurtherInformation()
        );
        contactDescription.append(SUPPORTING_DOCUMENTS + getAttachmentsStr(appealId, attachmentList));
    }

    private String getAttachmentsStr(String appealId, List<Attachment> attachmentList) {
        if (attachmentList == null || attachmentList.isEmpty()) {
            return "None";
        }
        final StringBuilder sb = new StringBuilder();
        attachmentList.forEach(attachment -> {
            sb.append("\n  - ").append(attachment.getName());
            Optional.ofNullable(attachment.getUrl()).ifPresent(url ->
                sb.append("\n    ").append(url).append("&a=").append(appealId));
        });
        return sb.toString();
    }
}
