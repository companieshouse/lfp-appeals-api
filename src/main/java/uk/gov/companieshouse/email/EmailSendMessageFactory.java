package uk.gov.companieshouse.email;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.exception.JsonException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.Region;
import uk.gov.companieshouse.service.CompanyProfileService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmailSendMessageFactory {


    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);
    private static final String TEAM_EMAIL_SUFFIX = "_TEAM_EMAIL";
    private static final String EXCEPTION_ERROR = "Error amending attachments ";
    private static final String LFP_APPEALS_API_APP_ID = "lfp-appeals-api";
    private static final String TOKEN_EMAIL_ADDRESS = "lfp-appeals@ch.gov.uk";
    private static final String LFP_APPEAL_INTERNAL_EMAIL_SUBJECT = "Appeal submitted - ";
    private static final String LFP_APPEAL_CONFIRMATION_EMAIL_SUBJECT = "Confirmation of your appeal - ";

    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
        "lfp-appeal-submission-internal";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE =
        "lfp-appeal-submission-confirmation";

    private final CompanyProfileService companyProfileService;
    private final EnvironmentReader environmentReader;


    public EmailSendMessageFactory(CompanyProfileService companyProfileService, EnvironmentReader environmentReader) {
        this.companyProfileService = companyProfileService;
        this.environmentReader = environmentReader;
    }

    /**
     * Creates a `SendEmail` object based on the provided appeal and email type.
     *
     * @param appeal    The appeal information used to compose the email.
     * @param emailType The type of email being sent (e.g., internal or confirmation).
     * @return A `SendEmail` object containing the email payload.
     */
    public SendEmail createMessage(Appeal appeal, final String emailType) {
        String penaltyReference = appeal.getPenaltyIdentifier().getPenaltyReference();
        LOGGER.info(String.format("Creating SendEmail payload for penalty reference %s", penaltyReference));
        var sendEmail = new SendEmail();
        sendEmail.setAppId(LFP_APPEALS_API_APP_ID);
        sendEmail.setMessageType(emailType);
        sendEmail.setEmailAddress(TOKEN_EMAIL_ADDRESS);
        sendEmail.setMessageId(UUID.randomUUID().toString());
        sendEmail.setJsonData(buildEmailContent(appeal, emailType));
        LOGGER.info(String.format("SendEmail payload created for penalty reference: %s", penaltyReference));
        return sendEmail;
    }

    /**
     * Build the email content from the appeal information.
     *
     * @param appeal    The appeal information used to compose the internal/confirmation emails.
     * @param emailType Type of email to build
     * @return Email content in JSON converted to String format
     */
    private String buildEmailContent(Appeal appeal, String emailType) {

        var penaltyIdentifier = appeal.getPenaltyIdentifier();
        String companyNumber = penaltyIdentifier.getCompanyNumber();
        String usersEmail = appeal.getCreatedBy().getEmailAddress();

        String emailTo;
        String emailSubject;
        if (emailType.equals(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE)) {
            emailTo = usersEmail;
            emailSubject = LFP_APPEAL_CONFIRMATION_EMAIL_SUBJECT;
        } else {
            emailTo = determineInternalEmailAddressToSendTo(companyNumber);
            emailSubject = LFP_APPEAL_INTERNAL_EMAIL_SUBJECT;
        }

        var jsonEmailContent = new JSONObject();
        try {
            jsonEmailContent.put("to", emailTo);
            jsonEmailContent.put("subject", emailSubject + companyNumber);
            CompanyProfileApi companyProfile = companyProfileService.getCompanyProfile(companyNumber);
            jsonEmailContent.put("companyName", companyProfile.getCompanyName());
            jsonEmailContent.put("companyNumber", companyNumber);
            jsonEmailContent.put("penaltyReference", penaltyIdentifier.getPenaltyReference());

            var jsonUserProfile = new JSONObject();
            jsonUserProfile.put("email", usersEmail);
            jsonEmailContent.put("userProfile", jsonUserProfile);

            if (emailType.equals(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE)) {
                jsonEmailContent.put("reasons", addReasonsData(appeal));
            }
        } catch (JSONException e) {
            throw new JsonException(EXCEPTION_ERROR, e);
        }

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("emailContent", jsonEmailContent.toString());
        LOGGER.debugContext(penaltyIdentifier.getPenaltyReference(), "Email Contents", logMap);

        return jsonEmailContent.toString();
    }

    /**
     * Works out the region email address to send the internal email to. Checks if the company
     * starts with the allowed region prefixes of NI or SC, reads the appropriate environment variable.
     * If it doesn't start with either NI or SC then the default email address environment variable is used.
     *
     * @param companyNumber Company number to check
     * @return Appropriate email address based on region
     */
    private String determineInternalEmailAddressToSendTo(String companyNumber) {
        for (Region reg : Region.values()) {
            if (companyNumber.startsWith(reg.name())) {
                return environmentReader.getMandatoryString(reg.name() + TEAM_EMAIL_SUFFIX);
            }
        }
        return environmentReader.getMandatoryString(Region.DEFAULT + TEAM_EMAIL_SUFFIX);
    }

    /**
     * Add reason information to be added to the email.
     *
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     * @return JSON of reason information
     */
    private JSONObject addReasonsData(Appeal appeal) {

        var appealReason = appeal.getReason();
        var otherReason = appealReason.getOther();

        var reason = new JSONObject();
        var reasonData = new JSONObject();
        try {
            reasonData.put("name", appeal.getCreatedBy().getName());
            if (otherReason != null) {
                reasonData.put("relationshipToCompany", appeal.getCreatedBy().getRelationshipToCompany());
                reasonData.put("title", otherReason.getTitle());
                reasonData.put("description", otherReason.getDescription());
                reasonData.put("attachments", addAttachments(appeal.getId(), otherReason.getAttachments()));
                reason.put("other", reasonData);
            } else {
                var illnessReason = appealReason.getIllness();
                reasonData.put("illPerson", illnessReason.getIllPerson());
                reasonData.put("illnessStart", illnessReason.getIllnessStart());
                reasonData.put("illnessEnd", illnessReason.getIllnessEnd());
                reasonData.put("description", illnessReason.getIllnessImpactFurtherInformation());
                reasonData.put("attachments", addAttachments(appeal.getId(), illnessReason.getAttachments()));
                reason.put("illness", reasonData);
            }
        } catch (JSONException e) {
            throw new JsonException(EXCEPTION_ERROR, e);
        }

        return reason;
    }

    /**
     * Add any attachment links to be added to the email.
     *
     * @param list The appeal information used to compose the internal/confirmation emails.
     * @return JSON array containing any links to attachments.
     */
    private JSONArray addAttachments(String appealId, List<Attachment> attachments) {

        var attachmentArray = new JSONArray();

        attachments.forEach(a -> {
            var jsonAttachments = new JSONObject();
            try {
                jsonAttachments.put("name", a.getName());
                jsonAttachments.put("url", a.getUrl() + "&a=" + appealId);
            } catch (JSONException e) {
                throw new JsonException(EXCEPTION_ERROR, e);
            }
            attachmentArray.put(jsonAttachments);
        });

        return attachmentArray;
    }


}
