package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.email.EmailSend;
import uk.gov.companieshouse.email.EmailSendMessageProducer;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;

/**
 * Communicates with <code>chs-email-sender</code> via the <code>send-email</code> Kafka topic to
 * trigger the sending of emails.
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    private static final String LFP_APPEALS_API_APP_ID =
    		"lfp-appeals-api";
    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
            "lfp-appeal-submission-internal";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE =
            "lfp-appeal-submission-confirmation";

    /**
     * This email address is supplied only to satisfy Avro contract.
     */
    private static final String TOKEN_EMAIL_ADDRESS = "lfp-appeals@ch.gov.uk";

     private final EmailSendMessageProducer producer;


    public EmailService(EmailSendMessageProducer producer) {
        this.producer = producer;
    }

    /**
     * Sends out a certificate or certified copy order confirmation email.
     *
     * @param appeal the order information used to compose the order confirmation email.
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */
    public void sendAppealEmails(final Appeal appeal)
            throws JsonProcessingException, InterruptedException, ExecutionException, SerializationException {

    	sendInternalConfirmationEmail(appeal);
    	//sendExternalConfirmationEmail(appeal);
    }

    /**
     * Sends out a certificate or certified copy order confirmation email.
     * @param messageType 
     *
     * @param appeal the order information used to compose the order confirmation email.
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */
    private EmailSend getEmailHeader(String messageType) {

        EmailSend email = new EmailSend();

        email.setAppId(LFP_APPEALS_API_APP_ID);
        email.setMessageType(messageType);
        email.setEmailAddress(TOKEN_EMAIL_ADDRESS);
        email.setMessageId(UUID.randomUUID().toString());
        email.setCreatedAt(LocalDateTime.now().toString());
        
        return email;
    }

    public void sendInternalConfirmationEmail(final Appeal appeal)
    		throws SerializationException, ExecutionException, InterruptedException {

        final EmailSend email = getEmailHeader(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);

        email.setData(buildEmailContent(appeal));

        producer.sendMessage(email, appeal.getPenaltyIdentifier().getPenaltyReference());
    }

    public void sendExternalConfirmationEmail(final Appeal appeal)
    		throws SerializationException, ExecutionException, InterruptedException {

        final EmailSend email = getEmailHeader(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE);

        email.setData(buildEmailContent(appeal));

        producer.sendMessage(email, appeal.getPenaltyIdentifier().getPenaltyReference());
    }

    /**
     * Sends out a certificate or certified copy order confirmation email.
     *
     * @param appeal the order information used to compose the order confirmation email.
     * @throws ServiceException 
     * @throws JSONException 
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */    
	private String buildEmailContent(Appeal appeal) {

		String email = appeal.getCreatedBy().getEmailAddress();
		PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
		String companyNumber = penaltyIdentifier.getCompanyNumber();

		JSONObject jsonEmailContent = new JSONObject();
		jsonEmailContent.put("to", email);
		jsonEmailContent.put("subject", "Appeal submitted - " + companyNumber);
		
		JSONObject jsonBody = new JSONObject();
    	jsonBody.put("templateName", LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);

        JSONObject jsonTemplateData = new JSONObject();
        jsonTemplateData.put("companyName", "company name");//getCompanyName(companyNumber));
        jsonTemplateData.put("companyNumber", companyNumber);
        jsonTemplateData.put("penaltyReference", penaltyIdentifier.getPenaltyReference());

        JSONObject jsonUserProfile = new JSONObject();
        jsonUserProfile.put("email", email);
        jsonTemplateData.put("userProfile", jsonUserProfile);
        jsonTemplateData.put("reasons", addReasonsData(appeal));

        jsonBody.put("templateData", jsonTemplateData);
        jsonEmailContent.put("body", jsonBody);
        
        LOGGER.info("JSON Array: " + jsonEmailContent.toString());
        
        return jsonEmailContent.toString();
	}
	
	private JSONObject addReasonsData(Appeal appeal) {

		Reason appealReason = appeal.getReason();
		
		JSONObject jsonReason = new JSONObject();
		jsonReason.put("name", appeal.getCreatedBy().getName());
		if( appealReason.getOther() != null ) {
			jsonReason.put("relationshipToCompany", appeal.getCreatedBy().getRelationshipToCompany());
			jsonReason.put("title", appeal.getReason().getOther().getTitle());
			jsonReason.put("description", appeal.getReason().getOther().getDescription());
		} else {
			jsonReason.put("illPerson", appeal.getReason().getIllness().getIllPerson());
			jsonReason.put("illnessStart", appeal.getReason().getIllness().getIllnessStart());
			jsonReason.put("illnessEnd", appeal.getReason().getIllness().getIllnessEnd());		
		}
		jsonReason.put("description", appeal.getReason().getOther().getDescription());
		jsonReason.put("attachments", addAttachments(appeal));
        
		JSONObject fullReason = new JSONObject();
		if( appealReason.getOther() != null ) {
			fullReason.put("other", jsonReason);
		} else {
			fullReason.put("illness", jsonReason);
		}
        return fullReason;
		
	}
    
	private JSONArray addAttachments(Appeal appeal) {

		Reason reason = appeal.getReason();

		JSONArray attachmentArray = new JSONArray();

		List<Attachment> attachments;
		if( reason.getOther() != null ) {
			attachments = reason.getOther().getAttachments();
		} else {
			attachments = reason.getIllness().getAttachments();
		}
		attachments.stream().forEach(a -> {
			JSONObject jsonAttachments = new JSONObject();
			jsonAttachments.put("name", a.getName());
			jsonAttachments.put("url", a.getUrl() + "&a=" + appeal.getId());
			attachmentArray.put(jsonAttachments);
		});
		
		return attachmentArray;
	}
//	private String getCompanyName(String companyNumber)
//		throws ServiceException {
//        try {
////            Map<String, Object> logMap = new HashMap<>();
////            logMap.put(LOG_COMPANY_NUMBER_KEY, companyNumber);
//
//            ApiClient apiClient = ApiSdkManager.getSDK();
//
//            String companyProfileUrl = String.format("/company/%s", companyNumber);
//
////            apiLogger.infoContext(
////                    requestId,
////                    "Retrieving company details from the SDK",
////                    logMap
////            );
//
//            return apiClient.company().get(companyProfileUrl).execute().getData().getCompanyName();
//        } catch (IOException | URIValidationException e) {
////            apiLogger.errorContext(
////                    requestId,
////                    e);
//
//            throw new ServiceException(
//                    String.format("Problem retrieving company details from the SDK for %s %s",
//                            "company-number", companyNumber),
//                    e);
//        }
//    }
}
