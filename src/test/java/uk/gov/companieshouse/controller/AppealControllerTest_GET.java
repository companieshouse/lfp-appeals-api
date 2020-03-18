package uk.gov.companieshouse.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.service.AppealService;
import uk.gov.companieshouse.util.TestUtil;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class AppealControllerTest_GET {

    private static final String APPEALS_URI = "/companies/{company-id}/appeals";
    private static final String TEST_RESOURCE_ID = "1";
    private static final String TEST_COMPANY_ID = "12345678";

    @MockBean
    private AppealService appealService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenAppealExists_return200() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenReturn(Optional.of(TestUtil.getValidAppeal()));

        String validAppeal = TestUtil.asJsonString("src/test/resources/data/validAppeal.json");

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, TEST_RESOURCE_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().json(validAppeal));
    }

    @Test
    public void whenAppealDoesNotExist_return200() throws Exception {

        when(appealService.getAppeal(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(get(APPEALS_URI + "/{id}", TEST_COMPANY_ID, TEST_RESOURCE_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").doesNotExist());
    }
}
