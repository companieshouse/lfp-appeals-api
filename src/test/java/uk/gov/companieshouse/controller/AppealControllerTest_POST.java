package uk.gov.companieshouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.service.AppealService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class AppealControllerTest_POST {

    private static final String APPEALS_URI = "/companies/{company-id}/appeals";
    private static final String IDENTITY_HEADER = "ERIC-identity";
    private static final String TEST_USER_ID = "1234";
    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_RESOURCE_ID = "1";

    @MockBean
    private AppealService appealService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();
    private String validAppeal;
    private List<Attachment> attachments;

    @BeforeEach
    public void setUp() throws Exception {

        when(appealService.saveAppeal(any(Appeal.class), any(String.class))).thenReturn(TEST_RESOURCE_ID);

        validAppeal = asJsonString("src/test/resources/data/validAppeal.json");
        List<Object> listOfAttachmentObjects = mapper.readValue(
            new File("src/test/resources/data/listOfValidattachments.json"), 
            ArrayList.class);

        attachments = listOfAttachmentObjects.stream()
            .map(obj -> mapper.convertValue(obj, Attachment.class))
            .collect(Collectors.toList());
    }

    @Test
    public void whenValidInput_return201() throws Exception {

        String validAppealWithAttachments = asJsonString("src/test/resources/data/validAppeal.json", appeal -> {        
            appeal.getReason().getOther().setAttachments(attachments);
            return appeal;
        });

        List<String> validAppeals = List.of(
            validAppeal,
            validAppealWithAttachments
        );

        for (String appeal : validAppeals) {
            mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(createHttpHeaders())
                .content(appeal))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                + TEST_RESOURCE_ID));
        }
        
    }

    @Test
    public void whenNullEricIdentityHeader_return400() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(validAppeal))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Missing request header 'ERIC-identity' for method parameter of type String"));
    }

    @Test
    public void whenBlankEricIdentityHeader_return401() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(IDENTITY_HEADER, "")
            .content(validAppeal))
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

        final String invalidAppeal = asJsonString
            ("src/test/resources/data/invalidAppeal_penaltyIdentifierNull.json");

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidAppeal))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json("{'penaltyIdentifier':'penaltyIdentifier must not be null'}"));
    }

    @Test
    public void whenInvalidAttachment_return422() throws Exception {

        final String invalidAppeal = asJsonString
            ("src/test/resources/data/validAppeal.json", appeal -> {
                final Attachment invalidAttachment = attachments.get(0);
                invalidAttachment.setName("");
                appeal.getReason().getOther().setAttachments(List.of(invalidAttachment));
                return appeal;
            });
        
        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidAppeal))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json("{'reason.other.attachments[0].name':'name must not be blank.'}"));
    }

    @Test
    public void whenExceptionFromService_return500() throws Exception {

        when(appealService.saveAppeal(any(Appeal.class), any(String.class)))
            .thenThrow(Exception.class);

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(validAppeal))
            .andExpect(status().isInternalServerError());
    }

    private String asJsonString(final String pathname, final Function<Appeal, Appeal> appealModifier) {
        try {
            final Appeal appeal = mapper.readValue(new File(pathname), Appeal.class);
            return new ObjectMapper().writeValueAsString(appealModifier.apply(appeal));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String asJsonString(final String pathname) {
        return asJsonString(pathname, Function.identity());
    }

    private HttpHeaders createHttpHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(IDENTITY_HEADER, TEST_USER_ID);
        return httpHeaders;
    }
}
