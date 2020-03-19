package uk.gov.companieshouse.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.repository.AppealRepository;
import uk.gov.companieshouse.util.TestUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
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
    public static final String TEST_COMPANY_ID = "12345678";
    public static final String TEST_PENALTY_REFERENCE = "A12345678";
    public static final String TEST_REASON_TITLE = "This is a title";
    public static final String TEST_REASON_DESCRIPTION = "This is a description";

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealRepository appealRepository;

    @Test
    public void testCreateAppeal_returnsResourceId() throws Exception {

        final ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        final Appeal appeal = TestUtil.getValidAppeal();
        appeal.setId(TEST_RESOURCE_ID);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        appealService.saveAppeal(appeal, TEST_ERIC_ID);

        verify(appealRepository).insert(appealArgumentCaptor.capture());

        assertAll("Create appeal sets created by and created at",
            () -> assertEquals(TEST_ERIC_ID, appealArgumentCaptor.getValue().getCreatedBy().getId()),
            () -> assertNotNull(appealArgumentCaptor.getValue().getCreatedAt()));

        assertAll("Create appeal returns resource id",
            () -> assertNotNull(appealArgumentCaptor.getValue().getId()),
            () -> assertEquals(TEST_RESOURCE_ID, appealArgumentCaptor.getValue().getId()));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {

        final Appeal appeal = TestUtil.getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        final Appeal appeal = TestUtil.getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(null);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testGetAppealById_returnsAppeal() {

        final Appeal validAppeal = TestUtil.getValidAppeal();

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.of(validAppeal));

        final Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertTrue(appealOpt.isPresent());

        Appeal appeal = appealOpt.get();

        assertAll("Get appeal returns appeal with resource ID",
            () -> assertEquals(TEST_PENALTY_REFERENCE, appeal.getPenaltyIdentifier().getPenaltyReference()),
            () -> assertEquals(TEST_COMPANY_ID, appeal.getPenaltyIdentifier().getCompanyNumber()),
            () -> assertEquals(TEST_REASON_TITLE, appeal.getReason().getOther().getTitle()),
            () -> assertEquals(TEST_REASON_DESCRIPTION, appeal.getReason().getOther().getDescription()));
    }

    @Test
    public void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        final Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertFalse(appealOpt.isPresent());
        assertEquals(Optional.empty(), appealOpt);
    }
}
