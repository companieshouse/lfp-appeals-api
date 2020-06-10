package uk.gov.companieshouse.client;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.model.ChipsContact;

@Component
public class ChipsRestClient {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ChipsRestClient.class);

    private final RestTemplate restTemplate;

    @Autowired
    public ChipsRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> createContactInChips(ChipsContact contactDetails, String chipsUri) {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<ChipsContact> entity = new HttpEntity<>(contactDetails, headers);

        ResponseEntity<String> responseEntity;
        try {
            LOGGER.debug("Making a POST request to: {} for company number: {}", chipsUri, contactDetails.getCompanyNumber());
            responseEntity = restTemplate.postForEntity(chipsUri, entity, String.class);
        } catch (Exception e) {
            LOGGER.error("Unable to create contact in CHIPS for company number: {}, error: {}",
                contactDetails.getCompanyNumber(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return responseEntity;
    }
}
