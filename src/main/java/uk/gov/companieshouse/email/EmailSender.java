package uk.gov.companieshouse.email;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.client.EmailClient;
import uk.gov.companieshouse.exception.EmailSendException;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.logging.LoggingUtils;
import uk.gov.companieshouse.model.Appeal;

@Service
public class EmailSender {

    private static final String EXCEPTION_MESSAGE = "email-send message could not be sent via chs-kafka api for appeal with penalty reference - %s";
    private final EmailSendMessageFactory emailSendMessageFactory;
    private final EmailClient emailClient;

    public EmailSender(EmailSendMessageFactory emailSendMessageFactory, EmailClient emailClient) {
        this.emailSendMessageFactory = emailSendMessageFactory;
        this.emailClient = emailClient;
    }

    /**
     * Sends an email message to the CHS Kafka API.
     *
     * <p>This method creates a message using the provided {@link Appeal} object and emailType,
     * logs the process, and sends the message using the {@link EmailClient}. If an error occurs during
     * the message creation or sending process, a {@link ServiceException} is thrown.</p>
     *
     * @param appeal    the penalty reference associated with the email.
     * @param emailType the {@link String}  containing the type of email.
     * @throws ServiceException if the email message could not be sent due to an {@link EmailSendException}.
     */
    public void sendMessage(Appeal appeal, final String emailType) {
        try {
            String penaltyReference = appeal.getPenaltyIdentifier().getPenaltyReference();
            LoggingUtils.logWithPenaltyReference("creating message via chs-kafka api email for penaltyReference", penaltyReference);
            var sendEmail = emailSendMessageFactory.createMessage(appeal, emailType);
            LoggingUtils.logWithPenaltyReference("Sending message via chs-kafka api email for penaltyReference", penaltyReference);
            emailClient.sendEmail(sendEmail);
        } catch (EmailSendException e) {
            final var errorMessage
                = String.format(EXCEPTION_MESSAGE, appeal);
            throw new ServiceException(errorMessage, e);
        }
    }
}
