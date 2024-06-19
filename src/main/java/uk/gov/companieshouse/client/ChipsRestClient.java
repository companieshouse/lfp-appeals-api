package uk.gov.companieshouse.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.ChipsContact;

@Component
public class ChipsRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);
    private static final String CHIPS_ERROR_MESSAGE = "Failed to create contact in CHIPS for company number: %s";

    private final RestTemplate restTemplate;

    @Autowired
    public ChipsRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createContactInChips(ChipsContact contactDetails, String chipsUri) {

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(contactDetails, new HttpHeaders());
        final String companyNumber = contactDetails.getCompanyNumber();
        final ResponseEntity<String> responseEntity;

        try {
            LOGGER.debug("Making a POST request to: " + chipsUri + " with company number:  " + companyNumber);
            responseEntity = restTemplate.postForEntity(chipsUri, entity, String.class);
        } catch (Exception ex) {
            throw new ChipsServiceException(String.format(CHIPS_ERROR_MESSAGE, companyNumber), ex);
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new ChipsServiceException(String.format(CHIPS_ERROR_MESSAGE, companyNumber));
        }
    }
}
