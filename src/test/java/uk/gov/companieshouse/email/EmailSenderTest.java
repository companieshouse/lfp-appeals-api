package uk.gov.companieshouse.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.client.EmailClient;
import uk.gov.companieshouse.exception.EmailSendException;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailSenderTest {

    private static final String INTERNAL = "internal";
    @Mock
    private EmailSendMessageFactory emailSendMessageFactory;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private EmailSender emailSender;

    private Appeal appeal;

    @BeforeEach
    void setUp() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = TestUtil.createReasonWithOther();
        appeal = TestUtil.createAppeal(createdBy, reason);
    }

    @Test
    void testSendMessageSuccess() {
        // Arrange
        SendEmail sendEmail = mock(SendEmail.class);
        when(emailSendMessageFactory.createMessage(any(), any())).thenReturn(sendEmail);
        // Act
        emailSender.sendMessage(appeal, INTERNAL);
        // Assert
        verify(emailSendMessageFactory, times(1)).createMessage(appeal, INTERNAL);
        verify(emailClient, times(1)).sendEmail(sendEmail);
    }

    @Test
    void testSendMessageThrowsServiceException() {
        // Arrange
        SendEmail sendEmail = mock(SendEmail.class);
        when(emailSendMessageFactory.createMessage(any(), any())).thenReturn(sendEmail);
        doThrow(new EmailSendException("Error sending email")).when(emailClient).sendEmail(sendEmail);

        // Act & Assert
        assertThrows(ServiceException.class, () -> emailSender.sendMessage(appeal, INTERNAL));

        verify(emailSendMessageFactory, times(1)).createMessage(any(), any());
        verify(emailClient, times(1)).sendEmail(sendEmail);
    }
}
