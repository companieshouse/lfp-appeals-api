package uk.gov.companieshouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;
import uk.gov.companieshouse.service.AppealService;
import uk.gov.companieshouse.service.ServiceException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AppealController.class)
public class AppealControllerTestIT {

    private static final String APPEALS_URI = "/companies/{company-id}/appeals";

    private static final String RESOURCE_ID_HEADER = "resource_id";
    private static final String USER_ID_HEADER = "Eric-identity";

    private static final String TEST_USER_ID = "1234";
    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_RESOURCE_ID = "555";
    private static final String TEST_PENALTY_REFERENCE = "A12345678";
    private static final String TEST_REASON_TITLE = "This is a title";
    private static final String TEST_REASON_DESCRIPTION = "This is a description";

    @MockBean
    private AppealService appealService;

    @MockBean
    private AppealRepository appealRepository;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws ServiceException {

        when(appealService.createAppeal(any(String.class), any(String.class), any(Appeal.class)))
            .thenReturn(TEST_RESOURCE_ID);
    }

    @Test
    public void whenValidInput_return201() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(appealAsJson))
            .andExpect(status().isCreated())
            .andExpect(header().string(RESOURCE_ID_HEADER, TEST_RESOURCE_ID));
    }

    @Test
    public void whenNullHeader_return400() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(appealAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Missing request header 'Eric-identity' for method parameter of type String"));
    }

    @Test
    public void whenNullRequestBody_return400() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders()))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void whenInvalidInput_return422() throws Exception {

        Appeal appeal = validAppeal();
        appeal.setPenaltyIdentifier(null);

        String appealAsJson = asJsonString(appeal);

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(appealAsJson))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json("{'penaltyIdentifier':'penaltyIdentifier must not be null'}"));
    }

    @Test
    public void whenExceptionFromService_return500() throws Exception {

        when(appealService.createAppeal(any(String.class), any(String.class), any(Appeal.class)))
            .thenThrow(ServiceException.class);

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(appealAsJson))
            .andExpect(status().isInternalServerError())
            .andExpect(header().doesNotExist(USER_ID_HEADER));
    }

    private Appeal validAppeal() {

        PenaltyIdentifier penaltyIdentifier = new PenaltyIdentifier();
        penaltyIdentifier.setPenaltyReference(TEST_PENALTY_REFERENCE);
        penaltyIdentifier.setCompanyNumber(TEST_COMPANY_ID);

        OtherReason otherReason = new OtherReason();
        otherReason.setTitle(TEST_REASON_TITLE);
        otherReason.setDescription(TEST_REASON_DESCRIPTION);

        Reason reason = new Reason();
        reason.setOther(otherReason);

        Appeal appeal = new Appeal();
        appeal.setPenaltyIdentifier(penaltyIdentifier);
        appeal.setReason(reason);

        return appeal;
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(USER_ID_HEADER, TEST_USER_ID);
        return httpHeaders;
    }
}
