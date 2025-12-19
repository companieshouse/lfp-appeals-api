package uk.gov.companieshouse.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.exception.EmailSendException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailClientTest {

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateSendEmailHandler emailHandler;

    @Mock
    private PrivateSendEmailPost emailPost;

    @Mock
    private ApiResponse<Void> apiResponse;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private EmailClient emailClient;

    @BeforeEach
    void setUp() {
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(internalApiClient.sendEmailHandler()).thenReturn(emailHandler);
        when(emailHandler.postSendEmail(anyString(), any(SendEmail.class))).thenReturn(emailPost);
    }

    @Test
    void testSendEmailSuccess() throws Exception {
        // Arrange
        SendEmail sendEmail = mock(SendEmail.class);
        when(apiResponse.getStatusCode()).thenReturn(200);
        when(emailPost.execute()).thenReturn(apiResponse);

        // Act
        emailClient.sendEmail(sendEmail);

        // Assert
        verify(emailPost, times(1)).execute();
    }

    @Test
    void testSendEmailThrowsEmailSendException() throws Exception {
        // Arrange
        SendEmail sendEmail = mock(SendEmail.class);
        when(emailPost.execute()).thenThrow(ApiErrorResponseException.class);

        // Act & Assert
        assertThrows(EmailSendException.class, () -> emailClient.sendEmail(sendEmail));
    }
}
