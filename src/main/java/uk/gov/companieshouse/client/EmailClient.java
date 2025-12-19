package uk.gov.companieshouse.client;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.exception.EmailSendException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Component
@Qualifier("emailClient")
public class EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);
    private final InternalApiClient internalApiClient;

    public EmailClient(@Qualifier("emailInternalApiClient") InternalApiClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    public void sendEmail(SendEmail sendEmail) throws EmailSendException {
        var requestId = getRequestId().orElse(UUID.randomUUID().toString());
        try {
            internalApiClient.getHttpClient().setRequestId(requestId);
            LOGGER.info(format("Sending email with request id: %s and data: %s",
                requestId, sendEmail.getJsonData()));
            var emailHandler = internalApiClient.sendEmailHandler();
            var emailPost = emailHandler.postSendEmail("/send-email", sendEmail);
            ApiResponse<Void> response = emailPost.execute();
            LOGGER.info(format("Posted '%s' email to CHS Kafka API: Response %d, Request ID: %s",
                sendEmail.getMessageType(), response.getStatusCode(), requestId));
        } catch (ApiErrorResponseException ex) {
            LOGGER.error(String.format("Error sending email with  data: %s and request-id: %s", sendEmail.getJsonData(), requestId), ex);
            throw new EmailSendException(ex.getMessage());
        }
    }

    private Optional<String> getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.getRequest().getHeader("x-request-id"));
    }

}
