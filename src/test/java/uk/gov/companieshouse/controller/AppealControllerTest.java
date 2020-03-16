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
import uk.gov.companieshouse.exception.AppealNotFoundException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;
import uk.gov.companieshouse.service.AppealService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AppealControllerTest {

    private static final String APPEALS_URI = "/companies/{company-id}/appeals";

    private static final String IDENTITY_HEADER = "ERIC-identity";

    private static final String TEST_USER_ID = "1234";
    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_RESOURCE_ID = "1";
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
    public void setUp() throws Exception {

        when(appealService.saveAppeal(any(String.class), any(Appeal.class), any(String.class)))
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
            .andExpect(header().string(HttpHeaders.LOCATION,"http://localhost/companies/12345678/appeals/" + TEST_RESOURCE_ID));
    }

    @Test
    public void whenNullEricIdentityHeader_return400() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(appealAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Missing request header 'ERIC-identity' for method parameter of type String"));
    }

    @Test
    public void whenBlankEricIdentityHeader_return401() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(IDENTITY_HEADER, "")
            .content(appealAsJson))
            .andExpect(status().isUnauthorized());
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

        when(appealService.saveAppeal(any(String.class), any(Appeal.class), any(String.class)))
            .thenThrow(Exception.class);

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(appealAsJson))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenAppealExists_return200() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenReturn(validAppeal());

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, TEST_RESOURCE_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().json(appealAsJson));
    }

    @Test
    public void whenAppealDoesNotExist_return404() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenThrow(AppealNotFoundException.class);

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, "1")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
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
        httpHeaders.add(IDENTITY_HEADER, TEST_USER_ID);
        return httpHeaders;
    }
}
