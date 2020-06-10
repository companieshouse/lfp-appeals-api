package uk.gov.companieshouse.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
public class ChipsRestClientTest {

    @InjectMocks
    private ChipsRestClient chipsRestClient;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testCreateContactInChips_() {
    // TODO
    }
}
