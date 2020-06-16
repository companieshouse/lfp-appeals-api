package uk.gov.companieshouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.service.AppealService;

import java.io.File;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppealControllerTest_GET {

    private final String APPEALS_URI = "/companies/{company-id}/appeals";
    private final String TEST_RESOURCE_ID = "1";
    private final String TEST_COMPANY_ID = "12345678";

    @MockBean
    private AppealService appealService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void whenAppealExists_return200() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenReturn(Optional.of(getValidAppeal()));

        final String validAppeal = asJsonString();

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, TEST_RESOURCE_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().json(validAppeal));
    }

    @Test
    public void whenAppealDoesNotExist_return404() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, TEST_RESOURCE_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist());
    }

    private String asJsonString() {
        try {
            Appeal appeal = mapper.readValue(new File("src/test/resources/data/validAppeal.json"), Appeal.class);
            return new ObjectMapper().writeValueAsString(appeal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Appeal getValidAppeal() {

        PenaltyIdentifier penaltyIdentifier = new PenaltyIdentifier();
        String TEST_PENALTY_REFERENCE = "A12345678";
        penaltyIdentifier.setPenaltyReference(TEST_PENALTY_REFERENCE);
        penaltyIdentifier.setCompanyNumber(TEST_COMPANY_ID);

        OtherReason otherReason = new OtherReason();
        String TEST_REASON_TITLE = "This is a title";
        otherReason.setTitle(TEST_REASON_TITLE);
        String TEST_REASON_DESCRIPTION = "This is a description";
        otherReason.setDescription(TEST_REASON_DESCRIPTION);

        Reason reason = new Reason();
        reason.setOther(otherReason);

        CreatedBy createdBy = new CreatedBy();
        createdBy.setId("123abc456");

        Appeal appeal = new Appeal();
        appeal.setPenaltyIdentifier(penaltyIdentifier);
        appeal.setReason(reason);
        appeal.setCreatedBy(createdBy);

        return appeal;
    }
}
