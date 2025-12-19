package uk.gov.companieshouse.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.email.EmailSender;
import uk.gov.companieshouse.model.Appeal;

/**
 * Communicates with <code>chs-email-sender</code> using  chs-kafka-api to
 * trigger the sending of emails.
 */
@Service
public class EmailService {

    private final EmailSender emailSender;

    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
        "lfp-appeal-submission-internal";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE =
        "lfp-appeal-submission-confirmation";

    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Sends out LFP Appeal confirmation and internal emails.
     *
     * @param appeal The appeal information used to compose the internal/confirmation emails.
     */
    public void sendAppealEmails(final Appeal appeal) {

        emailSender.sendMessage(appeal, LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);
        emailSender.sendMessage(appeal, LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE);
    }


}
