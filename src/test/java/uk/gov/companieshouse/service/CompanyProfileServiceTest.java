package uk.gov.companieshouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private CompanyResourceHandler companyResourceHandler;

    @Mock
    private CompanyGet companyGet;

    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;

    @InjectMocks
    private CompanyProfileService companyProfileService;

    private CompanyProfileApi dummyCompanyProfile;

    @BeforeEach
    void init() {
        CompanyProfileApi dummyCompanyProfile = new CompanyProfileApi();
        dummyCompanyProfile.setCompanyNumber(COMPANY_NUMBER);
        dummyCompanyProfile.setCompanyName("Company: " + COMPANY_NUMBER);
        dummyCompanyProfile.setJurisdiction("wales");
    }

    @DisplayName("Successfully return a company profile from the company profile SDK")
    @Test
    void testGetCompanyProfile() throws ApiErrorResponseException, URIValidationException, ServiceException {
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(dummyCompanyProfile);

        CompanyProfileApi returnedCompanyProfile = companyProfileService.getCompanyProfile(COMPANY_NUMBER);

        assertEquals(dummyCompanyProfile, returnedCompanyProfile);
    }

    @DisplayName("Throw a ServiceException when an ApiErrorResponseException on executing a GET on SDK")
    @Test
    void testGetCompanyProfileThrowsServiceExceptionIfCallFails()
            throws ApiErrorResponseException, URIValidationException {
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("Error")));

        assertThrows(
                ServiceException.class,
                () -> companyProfileService.getCompanyProfile(COMPANY_NUMBER)
        );
    }

    @DisplayName("Throw a ServiceException when an URIValidationException on executing a GET on SDK")
    @Test
    void testGetCompanyProfileThrowsServiceExceptionIfCallFails2()
            throws ApiErrorResponseException, URIValidationException {
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get("/company/" + COMPANY_NUMBER)).thenReturn(companyGet);
        when(companyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(
                ServiceException.class,
                () -> companyProfileService.getCompanyProfile(COMPANY_NUMBER)
        );
    }
}
