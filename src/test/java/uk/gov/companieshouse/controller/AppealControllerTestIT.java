package uk.gov.companieshouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.service.AppealService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppealApplication.class)
public class AppealControllerTestIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    AppealService appealService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenValidInput_return201() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post("/appeals")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Eric-identity", "1234")
            .content(appealAsJson))
            .andExpect(status().isCreated());
    }

    @Test
    public void whenNullHeader_return400() throws Exception {

        String appealAsJson = asJsonString(validAppeal());

        mockMvc.perform(post("/appeals")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(appealAsJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void whenNullRequestBody_return400() throws Exception {

        mockMvc.perform(post("/appeals")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Eric-identity", "1234"))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void whenInvalidInput_return422() throws Exception {

        Appeal appeal = validAppeal();
        appeal.setPenaltyIdentifier(null);

        String appealAsJson = asJsonString(appeal);

        mockMvc.perform(post("/appeals")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("Eric-identity", "1234")
            .content(appealAsJson))
            .andExpect(status().isUnprocessableEntity());
    }

    private Appeal validAppeal() {

        PenaltyIdentifier penaltyIdentifier = new PenaltyIdentifier();
        penaltyIdentifier.setPenaltyReference("A12345678");
        penaltyIdentifier.setCompanyNumber("12345678");

        OtherReason otherReason = new OtherReason();
        otherReason.setTitle("This is a title");
        otherReason.setDescription("This is a description");

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
}
