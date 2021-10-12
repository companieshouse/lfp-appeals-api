package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.email.EmailSend;
import uk.gov.companieshouse.email.EmailSendMessageProducer;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
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

    private static final String LFP_APPEALS_API_APP_ID = "lfp-appeals-api";
    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
            "lfp-appeal-submission-internal";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE =
            "lfp-appeal-submission-confirmation";
    private static final String LFP_APPEAL_INTERNAL_EMAIL_SUBJECT = "Appeal submitted - ";
    private static final String LFP_APPEAL_CONFIRMATION_EMAIL_SUBJECT = "Confirmation of your appeal - ";
    private static final String TEAM_EMAIL_SUFFIX = "_TEAM_EMAIL";

    // This email address is supplied only to satisfy Avro contract.
    private static final String TOKEN_EMAIL_ADDRESS = "lfp-appeals@ch.gov.uk";

    private final EmailSendMessageProducer producer;

    public EmailService(EmailSendMessageProducer producer) {
        this.producer = producer;
    }
    
    @Bean
    public static EnvironmentReader getEnvironmentReader() {
        return new EnvironmentReaderImpl();
    }

    /**
     * Sends out LFP Appeal confirmation and internal emails.
     *
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     * 
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */
    public void sendAppealEmails(final Appeal appeal)
            throws JsonProcessingException, InterruptedException, ExecutionException, SerializationException {

    	sendInternalConfirmationEmail(appeal);
    	sendExternalConfirmationEmail(appeal);
    }

    /**
     * Build email header data.
     * 
     * @param emailType Type of email being sent.
     * 
     * @return email header with data
     */
    private EmailSend getEmailHeader(String emailType) {

        EmailSend email = new EmailSend();

        email.setAppId(LFP_APPEALS_API_APP_ID);
        email.setMessageType(emailType);
        email.setEmailAddress(TOKEN_EMAIL_ADDRESS);
        email.setMessageId(UUID.randomUUID().toString());
        email.setCreatedAt(LocalDateTime.now().toString());
        
        return email;
    }

    /**
     * Send an internal lfp appeal email to kafka.
     * 
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     * 
     * @throws SerializationException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void sendInternalConfirmationEmail(final Appeal appeal)
    		throws SerializationException, ExecutionException, InterruptedException {

        final EmailSend email = getEmailHeader(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);

        email.setData(buildEmailContent(appeal, LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE));

        producer.sendMessage(email, appeal.getPenaltyIdentifier().getPenaltyReference());
    }


    /**
     * Send an external lfp appeal email to kafka.
     * 
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     * 
     * @throws SerializationException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void sendExternalConfirmationEmail(final Appeal appeal)
    		throws SerializationException, ExecutionException, InterruptedException {

        final EmailSend email = getEmailHeader(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE);

        email.setData(buildEmailContent(appeal, LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE));

        producer.sendMessage(email, appeal.getPenaltyIdentifier().getPenaltyReference());
    }

    /**
     * Build the email content from the appeal information.
     *
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     * @param emailType Type of email to build
     * 
     * @return Email content in JSON converted to String format
     * 
     * @throws ServiceException 
     * @throws JSONException 
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */    
	private String buildEmailContent(Appeal appeal, String emailType) {

		PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
		String companyNumber = penaltyIdentifier.getCompanyNumber();
		String usersEmail = appeal.getCreatedBy().getEmailAddress();

		String emailTo;
		String emailSubject;
		if( emailType.equals(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE)) {
			emailTo = usersEmail;
			emailSubject = LFP_APPEAL_CONFIRMATION_EMAIL_SUBJECT;
		} else {
			emailTo = determineInternalEmailAddressToSendTo(companyNumber);
			emailSubject = LFP_APPEAL_INTERNAL_EMAIL_SUBJECT;			
		}

		JSONObject jsonEmailContent = new JSONObject();
		jsonEmailContent.put("to", emailTo);
		jsonEmailContent.put("subject", emailSubject + companyNumber);
		jsonEmailContent.put("companyName", "company name");//getCompanyName(companyNumber));
		jsonEmailContent.put("companyNumber", companyNumber);
		jsonEmailContent.put("penaltyReference", penaltyIdentifier.getPenaltyReference());

        JSONObject jsonUserProfile = new JSONObject();
        jsonUserProfile.put("email", usersEmail);
        jsonEmailContent.put("userProfile", jsonUserProfile);
        
        if( emailType.equals(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE)) {
        	jsonEmailContent.put("reasons", addReasonsData(appeal));
        }
        
        LOGGER.info("JSON Array: " + jsonEmailContent.toString());
        
        return jsonEmailContent.toString();
	}
	
	/**
	 * Works out the region email address to send the internal email to. Based on the if the company
	 * starts with the allowed region prefixes of NI or SC, reads the appropriate environment variable.
	 * If it doesn't start with either NI or SC then the default email address environment variable is used.
	 * 
	 * @param companyNumber Company number to check
	 * 
	 * @return Appropriate email address based on region 
	 */
	private String determineInternalEmailAddressToSendTo(String companyNumber) {
		EnvironmentReader environmentReader = getEnvironmentReader();
		for( Region reg : Region.values()) {
			if(companyNumber.startsWith(reg.name())) {
				return environmentReader.getMandatoryString(reg.name() + TEAM_EMAIL_SUFFIX);
			}
		}
		return environmentReader.getMandatoryString(Region.DEFAULT + TEAM_EMAIL_SUFFIX);
	}
	
	/**
	 * Add reason information to be added to the email.
	 *  
	 * @param appeal The appeal information used to compose the internal/confirmation emails.
	 * 
	 * @return JSON of reason information
	 */
	private JSONObject addReasonsData(Appeal appeal) {

		Reason appealReason = appeal.getReason();
		
		JSONObject reasonData = new JSONObject();
		reasonData.put("name", appeal.getCreatedBy().getName());
		if( appealReason.getOther() != null ) {
			reasonData.put("relationshipToCompany", appeal.getCreatedBy().getRelationshipToCompany());
			reasonData.put("title", appeal.getReason().getOther().getTitle());
			reasonData.put("description", appeal.getReason().getOther().getDescription());
		} else {
			reasonData.put("illPerson", appeal.getReason().getIllness().getIllPerson());
			reasonData.put("illnessStart", appeal.getReason().getIllness().getIllnessStart());
			reasonData.put("illnessEnd", appeal.getReason().getIllness().getIllnessEnd());		
			reasonData.put("description", appeal.getReason().getIllness().getIllnessImpactFurtherInformation());
		}
		reasonData.put("attachments", addAttachments(appeal));
        
		JSONObject reason = new JSONObject();
		if( appealReason.getOther() != null ) {
			reason.put("other", reasonData);
		} else {
			reason.put("illness", reasonData);
		}
        return reason;
		
	}
    
	/**
	 * Add any attachment links to be added to the email.
	 * 
	 * @param appeal The appeal information used to compose the internal/confirmation emails.
	 * 
	 * @return JSON array containing any links to attachments.
	 */
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
