package uk.gov.companieshouse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyProfileService {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);	

    private static final UriTemplate GET_COMPANY_URI = new UriTemplate("/company/{companyNumber}");

    @Autowired
    InternalApiClient internalApiClient;

    public CompanyProfileApi getCompanyProfile(String companyNumber) {

    	LOGGER.debug("Get company profile for " + companyNumber);

        ApiClient apiClient = internalApiClient;

        CompanyProfileApi companyProfileApi;

        var uri = GET_COMPANY_URI.expand(companyNumber).toString();

        try {
            companyProfileApi = apiClient.company().get(uri).execute().getData();
        } catch (ApiErrorResponseException e) {
            throw new ServiceException("Error retrieving company profile", e);
        } catch (URIValidationException e) {
            throw new ServiceException("Invalid URI for company resource", e);
        }

        return companyProfileApi;
    }
}
