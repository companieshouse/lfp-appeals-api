package uk.gov.companieshouse.email;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.exception.EnvironmentVariableException;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.model.Region;
import uk.gov.companieshouse.service.CompanyProfileService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.TestData.EMAIL;
import static uk.gov.companieshouse.TestData.LFP_APPEALS_API;

@ExtendWith(MockitoExtension.class)
class EmailSendMessageFactoryTest {

    private static final String TEAM_EMAIL_SUFFIX = "_TEAM_EMAIL";
    private static final String EMAIL_TYPE = "email_type";
    private static final String SUBJECT = "subject";
    private static final String LFP_APPEALS_EMAIL = "lfp-appeals@ch.gov.uk";
    private static final String COMPANY_NAME = "companyName";
    private static final String COMPANY_NUMBER = "companyNumber";
    private static final String TO = "to";
    private static final String DEFAULT_EXAMPLE_COM = "default@example.com";
    private static final String DEFAULT_TEAM_EMAIL = "DEFAULT_TEAM_EMAIL";
    private static final String CONFIRMATION_APPEAL_MESSAGE = "Confirmation of your appeal - ";
    private static final String LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE = "lfp-appeal-submission-confirmation";
    private static final String LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE =
        "lfp-appeal-submission-internal";
    private static final String REGION_COMPANY_NUMBER = "SC123123";
    private static final String LFP_APPEAL_INTERNAL_EMAIL_SUBJECT = "Appeal submitted - ";
    @Mock
    private EnvironmentReader environmentReader;

    @Mock
    private CompanyProfileService companyProfileService;
    
    @Mock
    private URIValidationException uriValidationException;

    @Mock
    private ApiErrorResponseException apiErrorResponseException;

    @InjectMocks
    private EmailSendMessageFactory emailSendMessageFactory;

    private Appeal appeal;

    @BeforeEach
    void setUp() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = TestUtil.createReasonWithOther();
        appeal = TestUtil.createAppeal(createdBy, reason);
        emailSendMessageFactory = new EmailSendMessageFactory(companyProfileService, environmentReader);
    }

    @Test
    void testCreateMessageConfirmationEmail() throws JSONException {
        // Arrange
        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyName(TestData.COMPANY_NAME);
        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenReturn(companyProfile);

        // Act
        SendEmail sendEmail = emailSendMessageFactory.createMessage(appeal, LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE);

        // Assert
        assertEquals(LFP_APPEALS_API, sendEmail.getAppId());
        assertEquals(LFP_APPEAL_SUBMISSION_CONFIRMATION_MESSAGE_TYPE, sendEmail.getMessageType());
        assertEquals(LFP_APPEALS_EMAIL, sendEmail.getEmailAddress());
        assertNotNull(sendEmail.getJsonData());

        JSONObject jsonData = new JSONObject(sendEmail.getJsonData());
        assertEquals(EMAIL, jsonData.getString(TO));
        assertTrue(jsonData.getString(SUBJECT).contains(CONFIRMATION_APPEAL_MESSAGE));
        assertEquals(TestData.COMPANY_NAME, jsonData.getString(COMPANY_NAME));
        assertEquals(TestData.COMPANY_NUMBER, jsonData.getString(COMPANY_NUMBER));
    }

    @Test
    void testCreateMessageInternalEmail() throws JSONException {
        // Arrange
        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyName(TestData.COMPANY_NAME);
        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenReturn(companyProfile);
        when(environmentReader.getMandatoryString(DEFAULT_TEAM_EMAIL)).thenReturn(DEFAULT_EXAMPLE_COM);

        // Act
        SendEmail sendEmail = emailSendMessageFactory.createMessage(appeal, LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);

        // Assert
        assertEquals(LFP_APPEALS_API, sendEmail.getAppId());
        assertEquals(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE, sendEmail.getMessageType());
        assertEquals(LFP_APPEALS_EMAIL, sendEmail.getEmailAddress());
        assertNotNull(sendEmail.getJsonData());

        JSONObject jsonData = new JSONObject(sendEmail.getJsonData());
        assertEquals(DEFAULT_EXAMPLE_COM, jsonData.getString(TO));
        assertTrue(jsonData.getString(SUBJECT).contains(LFP_APPEAL_INTERNAL_EMAIL_SUBJECT));
        assertEquals(TestData.COMPANY_NAME, jsonData.getString(COMPANY_NAME));
        assertEquals(TestData.COMPANY_NUMBER, jsonData.getString(COMPANY_NUMBER));
    }

    @Test
    void testCreateMessageConfirmationEmailWithIllnessReason() throws JSONException {
        // Arrange
        CompanyProfileApi companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyName(REGION_COMPANY_NUMBER);
        when(companyProfileService.getCompanyProfile(REGION_COMPANY_NUMBER)).thenReturn(companyProfile);
        when(environmentReader.getMandatoryString(anyString())).thenReturn(EMAIL);

        appeal.setReason(TestUtil.createReasonWithIllness());
        appeal.getPenaltyIdentifier().setCompanyNumber(REGION_COMPANY_NUMBER);
        // Act
        SendEmail sendEmail = emailSendMessageFactory.createMessage(appeal, LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE);
        // Assert
        assertEquals(LFP_APPEALS_API, sendEmail.getAppId());
        assertEquals(LFP_APPEAL_SUBMISSION_INTERNAL_MESSAGE_TYPE, sendEmail.getMessageType());
        assertEquals(LFP_APPEALS_EMAIL, sendEmail.getEmailAddress());
        assertNotNull(sendEmail.getJsonData());

        JSONObject jsonData = new JSONObject(sendEmail.getJsonData());
        assertEquals(EMAIL, jsonData.getString(TO));
        assertTrue(jsonData.getString(SUBJECT).contains(LFP_APPEAL_INTERNAL_EMAIL_SUBJECT));
        assertEquals(REGION_COMPANY_NUMBER, jsonData.getString(COMPANY_NAME));
        assertEquals(REGION_COMPANY_NUMBER, jsonData.getString(COMPANY_NUMBER));
    }

    @DisplayName("Unsuccessfully send emails due to EnvironmentVariableException thrown when determining region email address")
    @Test
    void testGetRegionEmailAddressThrowsEnvironmentVariableException() {

        final EnvironmentVariableException exception = new EnvironmentVariableException(TestData.EXCEPTION_MESSAGE);

        when(environmentReader.getMandatoryString(Region.DEFAULT + TEAM_EMAIL_SUFFIX)).thenThrow(exception);

        EnvironmentVariableException thrown = assertThrows(EnvironmentVariableException.class, () -> emailSendMessageFactory.createMessage(appeal, EMAIL_TYPE));

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

    private void testExceptionHandingForGettingCompanyProfile(Exception exceptionToThrow, Exception exceptionToCheck) {

        when(companyProfileService.getCompanyProfile(TestData.COMPANY_NUMBER)).thenThrow(exceptionToThrow);
        when(environmentReader.getMandatoryString(Region.DEFAULT + TEAM_EMAIL_SUFFIX)).thenReturn(TestData.EMAIL);

        ServiceException thrown = assertThrows(ServiceException.class, () -> emailSendMessageFactory.createMessage(appeal, EMAIL_TYPE));

        assertEquals(exceptionToCheck, thrown.getCause());
        assertEquals(exceptionToThrow.getMessage(), thrown.getMessage());
    }
}
