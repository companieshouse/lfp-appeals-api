package uk.gov.companieshouse.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.model.ChipsContact;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

        chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL);

        verify(restTemplate, times(1)).postForEntity(TEST_CHIPS_URL, entity, String.class);
    }

    @Test
    public void testCreateContactInChips_restCallThrowsException() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        ChipsServiceException chipsServiceException = assertThrows(ChipsServiceException.class,
            () -> chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL));

        assertEquals("Failed to create contact in CHIPS for company number: " + TEST_COMPANY_NUMBER,
            chipsServiceException.getMessage());
    }

    @Test
    public void testCreateContactInChips_badRequestThrowsChipsServiceException() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        ChipsServiceException chipsServiceException = assertThrows(ChipsServiceException.class,
            () -> chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL));

        assertEquals("Failed to create contact in CHIPS for company number: " + TEST_COMPANY_NUMBER,
            chipsServiceException.getMessage());
    }

    @Test
    public void testCreateContactInChips_internalServerErrorThrowsChipsServiceException() {

        final ChipsContact chipsContact = createChipsContact();

        final HttpEntity<ChipsContact> entity = new HttpEntity<>(chipsContact, new HttpHeaders());

        when(restTemplate.postForEntity(TEST_CHIPS_URL, entity, String.class))
            .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        ChipsServiceException chipsServiceException = assertThrows(ChipsServiceException.class,
            () -> chipsRestClient.createContactInChips(chipsContact, TEST_CHIPS_URL));

        assertEquals("Failed to create contact in CHIPS for company number: " + TEST_COMPANY_NUMBER,
            chipsServiceException.getMessage());
    }

    private ChipsContact createChipsContact() {
        final ChipsContact chipsContact = new ChipsContact();
        chipsContact.setCompanyNumber(TEST_COMPANY_NUMBER);
        chipsContact.setContactDescription(TEST_DESCRIPTION);
        chipsContact.setDateReceived(TEST_DATE_RECEIVED);
        return chipsContact;
    }
}
