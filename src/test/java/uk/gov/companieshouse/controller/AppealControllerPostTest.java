package uk.gov.companieshouse.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.service.AppealService;

@SpringBootTest
@AutoConfigureMockMvc
class AppealControllerPostTest {

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
    void setUp() throws Exception {

        when(appealService.saveAppeal(any(Appeal.class), any(String.class))).thenReturn(TEST_RESOURCE_ID);

        validAppeal = asJsonString("src/test/resources/data/validAppeal.json");
        attachments = mapper.readValue(
            new File("src/test/resources/data/listOfValidAttachments.json"),
            new TypeReference<>() { }
        );
    }

    @Test
    void whenValidInput_return201() throws Exception {

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
                .andExpect(content().json(TEST_RESOURCE_ID))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                + TEST_RESOURCE_ID));
        }
    }

    /*@Test
    void whenOldPenaltyReference_return201() throws Exception {
        String validAppealWithAttachments = asJsonString("src/test/resources/data/validOldPenaltyReferenceAppeal.json", appeal -> {
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
                .andExpect(content().json(TEST_RESOURCE_ID))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                    + TEST_RESOURCE_ID));
        }
    }

    @Test
    void whenOldPenaltyReferenceWhitespace_return201() throws Exception {
        String validAppealWithAttachments = asJsonString("src/test/resources/data/validOldPenaltyReferenceWhitespaceAppeal.json", appeal -> {
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
                .andExpect(content().json(TEST_RESOURCE_ID))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                    + TEST_RESOURCE_ID));
        }
    }
*/
    @Test
    void whenOldPenaltyReference_return201() throws Exception {
        String validAppealWithAttachments = asJsonString("src/test/resources/data/validOldPenaltyReferenceAppeal.json", appeal -> {
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
                .andExpect(content().json(TEST_RESOURCE_ID))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                    + TEST_RESOURCE_ID));
        }
    }

    @Test
    void whenOldPenaltyReferenceWhitespace_return201() throws Exception {
        String validAppealWithAttachments = asJsonString("src/test/resources/data/validOldPenaltyReferenceWhitespaceAppeal.json", appeal -> {
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
                .andExpect(content().json(TEST_RESOURCE_ID))
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/companies/12345678/appeals/"
                    + TEST_RESOURCE_ID));
        }
    }

    @Test
    void whenNullEricIdentityHeader_return400() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(validAppeal))
            .andExpect(status().isBadRequest())
            .andExpect(status().reason("Missing request header 'ERIC-identity' for method parameter of type String"));
    }

    @Test
    void whenBlankEricIdentityHeader_return401() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(IDENTITY_HEADER, "")
            .content(validAppeal))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void whenNullRequestBody_return400() throws Exception {

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenInvalidInput_return422() throws Exception {

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
    void whenInvalidAttachment_return422() throws Exception {

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
            .andExpect(content().json("{'reason.other.attachments[0].name':'attachment name must not be blank'}"));
    }

    @Test
    void whenMultipleInvalidAttachments_return422() throws Exception {

        final String invalidAppeal = asJsonString
            ("src/test/resources/data/validAppeal.json", appeal -> {
                final Attachment invalidAttachment = attachments.get(0);
                final Attachment invalidAttachment2 = attachments.get(1);
                invalidAttachment.setName("");
                invalidAttachment2.setId("");
                appeal.getReason().getOther().setAttachments(List.of(invalidAttachment, invalidAttachment2));
                return appeal;
            });

        final String errMsg = "{'reason.other.attachments[0].name':'attachment name must not be blank'," +
            "'reason.other.attachments[1].id':'attachment id must not be blank'}";

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidAppeal))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json(errMsg));
    }

    @Test
    void whenMultipleMixInvalidAndValidAttachments_return422() throws Exception {

        final String invalidAppeal = asJsonString
            ("src/test/resources/data/validAppeal.json", appeal -> {
                final Attachment validAttachment = attachments.get(0);
                final Attachment invalidAttachment = attachments.get(1);
                invalidAttachment.setId("");
                appeal.getReason().getOther().setAttachments(List.of(validAttachment, invalidAttachment));
                return appeal;
            });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidAppeal))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json("{'reason.other.attachments[1].id':'attachment id must not be blank'}"));
    }

    @Test
    void whenInvalidAppealReason_return400() throws Exception {
        final String invalidAppeal = asJsonString
            ("src/test/resources/data/invalidAppealReason.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidAppeal))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenExceptionFromService_return500() throws Exception {

        when(appealService.saveAppeal(any(Appeal.class), any(String.class)))
            .thenThrow(RuntimeException.class);

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(validAppeal))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void whenIllnessReasonIncludesRelationship_return422() throws Exception {
        final String invalidRelationshipAppeal = asJsonString("src/test/resources/data/invalidRelationshipAppeal.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidRelationshipAppeal))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenIllnessReasonHasInvalidCreateByAppeal_return422() throws Exception {
        final String invalidCreateByAppeal = asJsonString("src/test/resources/data/invalidCreateByAppeal.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidCreateByAppeal))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().json("{\"createdBy.name\":\"createdBy.name must not be blank\"}"));
    }

    @Test
    void whenIllnessReasonHasInvalidIllPerson_return422() throws Exception {
        final String invalidIllPersonAppeal = asJsonString("src/test/resources/data/invalidIllPersonAppeal.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidIllPersonAppeal))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenIllnessReasonEndDateIsInvalid_return422() throws Exception {
        final String invalidEndDateAppeal = asJsonString("src/test/resources/data/invalidIllnessEndDate.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidEndDateAppeal))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenPenaltyReferenceIsInvalid_return422() throws Exception {
        final String invalidPenaltyReferenceAppeal = asJsonString("src/test/resources/data/invalidPenaltyReferenceAppeal.json", appeal -> { return appeal; });

        mockMvc.perform(post(APPEALS_URI, TEST_COMPANY_ID)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .headers(createHttpHeaders())
            .content(invalidPenaltyReferenceAppeal))
            .andExpect(status().isUnprocessableEntity());
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
