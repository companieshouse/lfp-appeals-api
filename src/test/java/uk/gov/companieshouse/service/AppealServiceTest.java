package uk.gov.companieshouse.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.client.ChipsRestClient;
import uk.gov.companieshouse.config.ChipsConfiguration;
import uk.gov.companieshouse.exception.ChipsServiceException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppealServiceTest {

    private static final String TEST_RESOURCE_ID = "1";
    private static final String TEST_ERIC_ID = "1";
    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_PENALTY_REFERENCE = "A12345678";
    private static final String TEST_REASON_TITLE = "This is a title";
    private static final String TEST_REASON_DESCRIPTION = "This is a description";
    private static final String TEST_EMAIL_ADDRESS = "someone@email.com";
    private static final String TEST_CHIPS_URL = "http://someurl";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static List<Attachment> testAttachments;

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealRepository appealRepository;

    @Mock
    private ChipsRestClient chipsRestClient;

    @Mock
    private ChipsConfiguration chipsConfiguration;

    @BeforeAll
    public static void beforeTest() throws IOException {
        testAttachments = mapper.readValue(
            new File("src/test/resources/data/listOfValidAttachments.json"),
            new TypeReference<>() { }
        );
    }

    @Test
    public void testCreateAppeal_returnsResourceId() throws Exception {

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        assertEquals(TEST_RESOURCE_ID, appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppeal_verify_createdBy_createdAt_setOnAppeal() throws Exception {

        final ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        appealService.saveAppeal(appeal, TEST_ERIC_ID);

        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertEquals(TEST_ERIC_ID, appealArgumentCaptor.getValue().getCreatedBy().getId());
        assertNotNull(appealArgumentCaptor.getValue().getCreatedAt());
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {

        final Appeal appeal = getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        final Appeal appeal = getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(null);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppealChipsEnabled_returnsResourceId() throws Exception {

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(chipsRestClient.createContactInChips(any(ChipsContact.class), anyString()))
            .thenReturn(ResponseEntity.accepted().build());

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        assertEquals(TEST_RESOURCE_ID, appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppealChipsEnabled_throwsExceptionIfChipsReturnsBadRequest() {

        final ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(chipsRestClient.createContactInChips(any(ChipsContact.class), anyString()))
            .thenReturn(ResponseEntity.badRequest().build());

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        Assertions.assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));

        verify(appealRepository).insert(appealArgumentCaptor.capture());
        verify(appealRepository).deleteById(appealArgumentCaptor.getValue().getId());
    }

    @Test
    public void testCreateAppealChipsEnabled_throwsExceptionIfChipsReturnsNull() {

        final ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        when(chipsRestClient.createContactInChips(any(ChipsContact.class), anyString())).thenReturn(null);

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        Assertions.assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));

        verify(appealRepository).insert(appealArgumentCaptor.capture());
        verify(appealRepository).deleteById(appealArgumentCaptor.getValue().getId());
    }

    @Test
    public void testCreateAppealChipsEnabled_throwsExceptionIfUnableToMakeRequest() {

        final ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        when(chipsConfiguration.isChipsEnabled()).thenReturn(true);
        when(chipsConfiguration.getChipsRestServiceUrl()).thenReturn(TEST_CHIPS_URL);

        final Appeal appeal = getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        Assertions.assertThrows(ChipsServiceException.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));

        verify(appealRepository).insert(appealArgumentCaptor.capture());
        verify(appealRepository).deleteById(appealArgumentCaptor.getValue().getId());
    }

    @Test
    public void testGetAppealById_returnsAppeal() {

        final Appeal validAppeal = getValidAppeal();

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.of(validAppeal));

        final Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertTrue(appealOpt.isPresent());

        Appeal appeal = appealOpt.get();

        assertEquals(TEST_PENALTY_REFERENCE, appeal.getPenaltyIdentifier().getPenaltyReference());
        assertEquals(TEST_COMPANY_ID, appeal.getPenaltyIdentifier().getCompanyNumber());
        assertEquals(TEST_REASON_TITLE, appeal.getReason().getOther().getTitle());
        assertEquals(TEST_REASON_DESCRIPTION, appeal.getReason().getOther().getDescription());
        assertFalse(testAttachments.isEmpty());
        assertEquals(testAttachments, appeal.getReason().getOther().getAttachments());
    }

    @Test
    public void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        final Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertFalse(appealOpt.isPresent());
        assertEquals(Optional.empty(), appealOpt);
    }

    @Test
    public void testBuildChipsContact() {

        Appeal appeal = getValidAppeal();

        ChipsContact chipsContact = appealService.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        assertEquals(expectedContactDescription(), chipsContact.getContactDescription());
    }

    private Appeal getValidAppeal() {

        PenaltyIdentifier penaltyIdentifier = new PenaltyIdentifier();
        penaltyIdentifier.setPenaltyReference(TEST_PENALTY_REFERENCE);
        penaltyIdentifier.setCompanyNumber(TEST_COMPANY_ID);

        OtherReason otherReason = new OtherReason();
        otherReason.setTitle(TEST_REASON_TITLE);
        otherReason.setDescription(TEST_REASON_DESCRIPTION);
        otherReason.setAttachments(testAttachments);

        Reason reason = new Reason();
        reason.setOther(otherReason);

        Appeal appeal = new Appeal();
        appeal.setPenaltyIdentifier(penaltyIdentifier);
        appeal.setReason(reason);
        appeal.setCreatedAt(LocalDateTime.now());
        CreatedBy createdBy = new CreatedBy();
        createdBy.setEmailAddress(TEST_EMAIL_ADDRESS);
        appeal.setCreatedBy(createdBy);

        return appeal;
    }

    private static String expectedContactDescription() {
        return "Appeal submitted" +
            "\n\nYour reference number is your company number " + TEST_COMPANY_ID +
            "\n\nCompany Number: " + TEST_COMPANY_ID +
            "\nEmail address: " + TEST_EMAIL_ADDRESS +
            "\n\nAppeal Reason" +
            "\nReason: " + TEST_REASON_TITLE +
            "\nFurther information: " + TEST_REASON_DESCRIPTION +
            "\nSupporting documents: None";
    }
}
