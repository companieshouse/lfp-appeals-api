package uk.gov.companieshouse.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AppealServiceTest {

    private static final String TEST_RESOURCE_ID = "1";
    private static final String TEST_ERIC_ID = "1";
    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_PENALTY_REFERENCE = "A12345678";
    private static final String TEST_REASON_TITLE = "This is a title";
    private static final String TEST_REASON_DESCRIPTION = "This is a description";

    private final ObjectMapper mapper = new ObjectMapper();
    private List<Attachment> testAttachments;

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealRepository appealRepository;

    @Before
    public void beforeTest() throws IOException {
        testAttachments = mapper.readValue(
            new File("src/test/resources/data/listOfValidAttachments.json"),
            List.class
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
        assertEquals(testAttachments, appeal.getReason().getOther().getAttachments());
    }

    @Test
    public void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        final Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertFalse(appealOpt.isPresent());
        assertEquals(Optional.empty(), appealOpt);
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

        return appeal;
    }
}
