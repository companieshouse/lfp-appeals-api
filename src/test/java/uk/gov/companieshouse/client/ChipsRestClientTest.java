package uk.gov.companieshouse.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.model.ChipsContact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ChipsRestClientTest {

    private static final String TEST_CHIPS_URL = "http://resttemplate/appeals";
    private static final String TEST_COMPANY_NUMBER = "12345678";
    private static final String TEST_DESCRIPTION = "Some description";
    private static final String TEST_DATE_RECEIVED = "2020-01-01";

    @InjectMocks
    private ChipsRestClient chipsRestClient;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testCreateContactInChips() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenReturn(ResponseEntity.accepted().build());

        final ResponseEntity<String> responseEntity = chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL);

        assertEquals(new ResponseEntity<String>(HttpStatus.ACCEPTED), responseEntity);
    }

    @Test
    public void testCreateContactInChips_badRequestReturns500Response() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        final ResponseEntity<String> responseEntity = chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL);

        assertEquals(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR), responseEntity);
    }

    @Test
    public void testCreateContactInChips_serverErrorReturns500Response() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        final ResponseEntity<String> responseEntity = chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL);

        assertEquals(new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR), responseEntity);
    }

    private ChipsContact createChipsContact() {
        final ChipsContact chipsContact = new ChipsContact();
        chipsContact.setCompanyNumber(TEST_COMPANY_NUMBER);
        chipsContact.setContactDescription(TEST_DESCRIPTION);
        chipsContact.setDateReceived(TEST_DATE_RECEIVED);
        return chipsContact;
    }
}
