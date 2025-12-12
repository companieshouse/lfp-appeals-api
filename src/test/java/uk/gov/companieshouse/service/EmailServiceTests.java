package uk.gov.companieshouse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.email.EmailSender;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @InjectMocks
    private EmailService emailService;
    @Mock
    private EmailSender emailSender;
    @Mock
    private ExecutionException executionException;
    @Mock
    private InterruptedException interruptedException;

    private CreatedBy createdBy;
    private Reason reason;
    private Appeal appeal;
    
    @BeforeEach
    public void setUp() {
        createdBy = TestUtil.buildCreatedBy();
        reason = TestUtil.createReasonWithOther();
        appeal = TestUtil.createAppeal(createdBy, reason);
    }
    
    @DisplayName("Successfully send an internal and external email")
    @Test
    void testSendInternalExternalEmailsSuccessfully() {

        doNothing().when(emailSender).sendMessage(ArgumentMatchers.any(Appeal.class), ArgumentMatchers.any());
        emailService.sendAppealEmails(appeal);
        verify(emailSender, times(2)).sendMessage(ArgumentMatchers.any(Appeal.class), ArgumentMatchers.any());
    }


    @DisplayName("Call to chs-kafka-api throws ExecutionException")
    @Test
    void testThrowServiceExceptionOfTypeExecutionExceptionWhenCallingCHSKafkaApi() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, executionException);

        testExceptionHandingForCHSKafkaApi(exception, executionException);
    }

    @DisplayName("Call to chs-kafka-api throws InterruptedException")
    @Test
    void testThrowServiceExceptionOfTypeInterruptedExceptionWhenCallingCHSKafkaApi() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, interruptedException);

        testExceptionHandingForCHSKafkaApi(exception, interruptedException);
    }

    private void testExceptionHandingForCHSKafkaApi(Exception exceptionToThrow, Exception exceptionToCheck) {

        doThrow(exceptionToThrow).when(emailSender).sendMessage(ArgumentMatchers.any(Appeal.class), ArgumentMatchers.any());
        ServiceException thrown = assertThrows(ServiceException.class, () -> emailService.sendAppealEmails(appeal));
        assertEquals(exceptionToCheck, thrown.getCause());
        assertEquals(exceptionToThrow.getMessage(), thrown.getMessage());
    }
}
