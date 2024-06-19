package uk.gov.companieshouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

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
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.email.EmailSend;
import uk.gov.companieshouse.email.EmailSendMessageProducer;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.exception.EnvironmentVariableException;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.model.Region;

@ExtendWith(MockitoExtension.class)
class EmailServiceTests {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private EmailSendMessageProducer emailSendMessageProducer;

    @Mock
    private EnvironmentReader environmentReader;
    
    @Mock
    private CompanyProfileService companyProfileService;
    
    @Mock
    private CompanyProfileApi companyProfileApi;
    
    @Mock
    private ApiErrorResponseException apiErrorResponseException;
    
    @Mock
    private URIValidationException uriValidationException;
    
    @Mock
    private SerializationException serializationException; 
    @Mock
    private ExecutionException executionException;
    @Mock
    private InterruptedException interruptedException;

    private CreatedBy createdBy;
    private Reason reason;
    private Appeal appeal;
    
    @BeforeEach
    private void setUp() {
        createdBy = TestUtil.buildCreatedBy();
        reason = TestUtil.createReasonWithOther();
        appeal = TestUtil.createAppeal(createdBy, reason);
    }
    
    @DisplayName("Successfully send an internal and external email")
    @Test
    void testSendInternalExternalEmailsSuccessfully() {
        
        doNothing().when(emailSendMessageProducer).sendMessage(ArgumentMatchers.any(EmailSend.class), ArgumentMatchers.anyString());

        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(environmentReader.getMandatoryString(Region.DEFAULT + EmailService.TEAM_EMAIL_SUFFIX)).thenReturn(TestData.EMAIL);

        emailService.sendAppealEmails(appeal);
        
        verify(emailSendMessageProducer, times(2)).sendMessage(ArgumentMatchers.any(EmailSend.class), ArgumentMatchers.anyString());
    }

    @DisplayName("Unsuccessfully send emails due to EnvironmentVariableException thrown when determining region email address")
    @Test
    void testGetRegionEmailAddressThrowsEnvironmentVariableException() {
        
        final EnvironmentVariableException exception = new EnvironmentVariableException(TestData.EXCEPTION_MESSAGE);

        when(environmentReader.getMandatoryString(Region.DEFAULT + EmailService.TEAM_EMAIL_SUFFIX)).thenThrow(exception);
        
        EnvironmentVariableException thrown = assertThrows(EnvironmentVariableException.class, () -> emailService.sendAppealEmails(appeal));

        assertEquals(exception.getCause(), thrown.getCause());
        assertEquals(exception.getMessage(), thrown.getMessage());
    }

    @DisplayName("Unsuccessfully send emails due to ServiceException (ApiErrorResponseException) thrown when retrieving company name")
    @Test
    void testGetCompanyNameThrowsServiceExceptionOfTypeApiValidationException() {
        
    	final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, apiErrorResponseException);
        
        testExceptionHandingForGettingCompanyProfile(exception, apiErrorResponseException);
    }

    @DisplayName("Unsuccessfully send emails due to ServiceException (URIValidationException) thrown when retrieving company name")
    @Test
    void testGetCompanyNameThrowsServiceExceptionOfTypeURIValidationException() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, uriValidationException);
        
        testExceptionHandingForGettingCompanyProfile(exception, uriValidationException);
    }
    
    /**
     * Common method for handling calling the kafka producer exception tests.
     * 
     * @param exceptionToThrow
     * @param exceptionToCheck
     */
    private void testExceptionHandingForGettingCompanyProfile(Exception exceptionToThrow, Exception exceptionToCheck) {

        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenThrow(exceptionToThrow);
        when(environmentReader.getMandatoryString(Region.DEFAULT + EmailService.TEAM_EMAIL_SUFFIX)).thenReturn(TestData.EMAIL);
        
        ServiceException thrown = assertThrows(ServiceException.class, () -> emailService.sendAppealEmails(appeal));

        assertEquals(exceptionToCheck, thrown.getCause());
        assertEquals(exceptionToThrow.getMessage(), thrown.getMessage());
    }

    @DisplayName("Call to kafka producer throws SerializationException")
    @Test
    void testThrowServiceExceptionOfTypeSerializationExceptionWhenCallingProducer() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, serializationException);
        
        testExceptionHandingForProducer(exception, serializationException);
    }
    
    @DisplayName("Call to kafka producer throws ExecutionException")
    @Test
    void testThrowServiceExceptionOfTypeExecutionExceptionWhenCallingProducer() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, executionException);
        
        testExceptionHandingForProducer(exception, executionException);
    }
    
    @DisplayName("Call to kafka producer throws InterruptedException")
    @Test
    void testThrowServiceExceptionOfTypeInterruptedExceptionWhenCallingProducer() {
        
        final ServiceException exception = new ServiceException(TestData.EXCEPTION_MESSAGE, interruptedException);
        
        testExceptionHandingForProducer(exception, interruptedException);
    }
    
    /**
     * Common method for handling calling the kafka producer exception tests.
     * 
     * @param exceptionToThrow
     * @param exceptionToCheck
     */
    private void testExceptionHandingForProducer(Exception exceptionToThrow, Exception exceptionToCheck) {
        
        doThrow(exceptionToThrow).when(emailSendMessageProducer).sendMessage(ArgumentMatchers.any(EmailSend.class), ArgumentMatchers.anyString());

        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenReturn(companyProfileApi);
        when(environmentReader.getMandatoryString(Region.DEFAULT + EmailService.TEAM_EMAIL_SUFFIX)).thenReturn(TestData.EMAIL);
        
        ServiceException thrown = assertThrows(ServiceException.class, () -> emailService.sendAppealEmails(appeal));

        assertEquals(exceptionToCheck, thrown.getCause());
        assertEquals(exceptionToThrow.getMessage(), thrown.getMessage());
    }
}
