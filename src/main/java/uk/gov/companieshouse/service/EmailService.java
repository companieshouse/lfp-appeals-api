package uk.gov.companieshouse.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.email.EmailSend;
import uk.gov.companieshouse.email.EmailSendMessageProducer;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.PenaltyIdentifier;

/**
 * Communicates with <code>chs-email-sender</code> via the <code>send-email</code> Kafka topic to
 * trigger the sending of emails.
 */
@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    private static final String LFP_APPEALS_API_APP_ID =
    		AppealApplication.APP_NAMESPACE;
    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
            "lfp-appeal-submission-internal";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE =
            "lfp-appeal-submission-confirmation";
    private static final String ITEM_TYPE_CERTIFICATE = "certificate";
    private static final String ITEM_TYPE_CERTIFIED_COPY = "certified-copy";
    private static final String ITEM_TYPE_MISSING_IMAGE_DELIVERY = "missing-image-delivery";

    /**
     * This email address is supplied only to satisfy Avro contract.
     */
    private static final String TOKEN_EMAIL_ADDRESS = "chs-orders@ch.gov.uk";

//    private final OrderDataToCertificateOrderConfirmationMapper orderToCertificateOrderConfirmationMapper;
//    private final OrderDataToItemOrderConfirmationMapper orderToItemOrderConfirmationMapper;
    private final ObjectMapper objectMapper;
    private final EmailSendMessageProducer producer;

//    @Value("${certificate.order.confirmation.recipient}")
//    private String certificateOrderRecipient;
//    @Value("${certified-copy.order.confirmation.recipient}")
//    private String certifiedCopyOrderRecipient;

    public EmailService(
//            final OrderDataToCertificateOrderConfirmationMapper orderToConfirmationMapper,
//            final OrderDataToItemOrderConfirmationMapper orderToItemOrderConfirmationMapper,
            final ObjectMapper objectMapper,
            final EmailSendMessageProducer producer) {
//        this.orderToCertificateOrderConfirmationMapper = orderToConfirmationMapper;
//        this.orderToItemOrderConfirmationMapper = orderToItemOrderConfirmationMapper;
        this.objectMapper = objectMapper;
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
     *
     * @param appeal the order information used to compose the order confirmation email.
     * @throws JsonProcessingException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws SerializationException
     */
    public void sendInternalConfirmationEmail(final Appeal appeal)
    		throws SerializationException, ExecutionException, InterruptedException {

        final EmailSend email = new EmailSend();

        email.setAppId(LFP_APPEALS_API_APP_ID);
        email.setMessageType(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);
        email.setEmailAddress(TOKEN_EMAIL_ADDRESS);
        email.setMessageId(UUID.randomUUID().toString());
        
        //email.setData(objectMapper.writeValueAsString(confirmation));
        email.setCreatedAt(LocalDateTime.now().toString());

        String orderReference = appeal.getPenaltyIdentifier().getPenaltyReference();
        //LoggingUtils.logWithOrderReference("Sending confirmation email for order", orderReference);
        producer.sendMessage(email, orderReference);
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
//    public void sendExternalConfirmationEmail(final Appeal appeal) {
//
//        final EmailSend email = new EmailSend();
//
//        email.setAppId(LFP_APPEALS_API_APP_ID);
//        email.setMessageType(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE);
//        email.setEmailAddress(TOKEN_EMAIL_ADDRESS);
//        email.setMessageId(UUID.randomUUID().toString());
//        
//        email.setData(objectMapper.writeValueAsString(confirmation));
//        email.setCreatedAt(LocalDateTime.now().toString());
//
//        String orderReference = appeal.getPenaltyIdentifier().getPenaltyReference();
//        //LoggingUtils.logWithOrderReference("Sending confirmation email for order", orderReference);
//        producer.sendMessage(email, orderReference);
//    }

//    private void buildData(Appeal appeal) {
//        Map<String, Object> data = new HashMap<>();
//
//        LocalDateTime submittedOn = appeal.getCreatedAt();
//
//        data.put("subject", "Appeal submitted - " + appeal.getPenaltyIdentifier().getCompanyNumber());
//        data.put("objection_id", objection.getId());
//        data.put("to", TOKEN_EMAIL_ADDRESS);
//        data.put("full_name", objection.getCreatedBy().getFullName());
//        data.put("share_identity", objection.getCreatedBy().isShareIdentity());
//        data.put("company_name", companyName);
//        data.put("company_number", objection.getCompanyNumber());
//        data.put("reason", objection.getReason());
//        data.put("attachments", objection.getAttachments());
//        data.put("attachments_download_url_prefix", emailAttachmentDownloadUrlPrefix);
//    }
    
	private void buildJson(Appeal appeal) throws JSONException {

		PenaltyIdentifier penaltyIdentifier = appeal.getPenaltyIdentifier();
		String companyNumber = penaltyIdentifier.getCompanyNumber();

		JSONArray jsonArray = new JSONArray();
		
		JSONObject jsonHead = new JSONObject();

		jsonHead.put("to", TOKEN_EMAIL_ADDRESS);
		jsonHead.put("subject", "Appeal submitted - " + companyNumber);
		jsonArray.put(jsonHead);
		
		JSONObject jsonBody = new JSONObject();

    	jsonBody.put("templateName", "lfp-appeal-submission-internal");
        //data.put("templateData", );
        JSONObject companyDetails = new JSONObject();
        //companyDetails.put("companyName", getCompanyName(companyNumber));
        companyDetails.put("companyNumber", companyNumber);
        companyDetails.put("penaltyReference", penaltyIdentifier.getPenaltyReference());
        jsonArray.put(companyDetails);
        JSONObject userProfile = new JSONObject();
        userProfile.put("email", appeal.getCreatedBy().getEmailAddress());
        jsonArray.put(userProfile);
//        data.put("userProfile: {
//                email: userProfile.email
//            },
//            reasons: buildEmailReasonContent(appeal)
//    	
//		json.t
        
        LOGGER.info("JSON Array: " + jsonArray.toString());
		
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

    /**
     * Builds the order confirmation and email based on the order provided.
     * @param order the order for which an email confirmation is to be sent
     * @return a {@link OrderConfirmationAndEmail} holding both the confirmation and its email envelope
     */
//    private OrderConfirmationAndEmail buildOrderConfirmationAndEmail(final OrderData order) {
//        final String descriptionId = order.getItems().get(0).getDescriptionIdentifier();
//        final EmailSend email = new EmailSend();
//        final OrderConfirmation confirmation;
//        switch (descriptionId) {
//            case ITEM_TYPE_CERTIFICATE:
//                confirmation = orderToCertificateOrderConfirmationMapper.orderToConfirmation(order);
//                confirmation.setTo(certificateOrderRecipient);
//                email.setAppId(LFP_APPEALS_API_APP_ID);
//                email.setMessageType(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);
//                return new OrderConfirmationAndEmail(confirmation, email);
//            case ITEM_TYPE_CERTIFIED_COPY:
//                confirmation = orderToItemOrderConfirmationMapper.orderToConfirmation(order);
//                confirmation.setTo(certifiedCopyOrderRecipient);
//                email.setAppId(CERTIFIED_COPY_ORDER_NOTIFICATION_API_APP_ID);
//                email.setMessageType(CERTIFIED_COPY_ORDER_NOTIFICATION_API_MESSAGE_TYPE);
//                return new OrderConfirmationAndEmail(confirmation, email);
//            case ITEM_TYPE_MISSING_IMAGE_DELIVERY:
//                confirmation = orderToItemOrderConfirmationMapper.orderToConfirmation(order);
//                confirmation.setTo(missingImageDeliveryOrderRecipient);
//                email.setAppId(MISSING_IMAGE_DELIVERY_ORDER_NOTIFICATION_API_APP_ID);
//                email.setMessageType(MISSING_IMAGE_DELIVERY_ORDER_NOTIFICATION_API_MESSAGE_TYPE);
//                return new OrderConfirmationAndEmail(confirmation, email);
//            default:
//                final Map<String, Object> logMap = LoggingUtils.createLogMapWithOrderReference(order.getReference());
//                final String error = "Unable to determine order confirmation type from description ID " +
//                        descriptionId + "!";
//                LOGGER.error(error, logMap);
//                throw new ServiceException(error);
//        }
//    }

}
