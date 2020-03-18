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

        ArgumentCaptor<Appeal> appealArgumentCaptor = ArgumentCaptor.forClass(Appeal.class);

        Appeal appeal = TestUtil.getValidAppeal();
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

        Appeal appeal = TestUtil.getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(appeal);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        Appeal appeal = TestUtil.getValidAppeal();

        when(appealRepository.insert(any(Appeal.class))).thenReturn(null);

        assertThrows(Exception.class, () -> appealService.saveAppeal(appeal, TEST_ERIC_ID));
    }

    @Test
    public void testGetAppealById_returnsAppeal() {

        Appeal validAppeal = TestUtil.getValidAppeal();

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.of(validAppeal));

        Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertTrue(appealOpt.isPresent());

        Appeal appeal = appealOpt.get();

        assertAll("Get appeal returns appeal with resource ID",
            () -> assertEquals(appeal.getPenaltyIdentifier().getPenaltyReference(), TEST_PENALTY_REFERENCE),
            () -> assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), TEST_COMPANY_ID),
            () -> assertEquals(appeal.getReason().getOther().getTitle(), TEST_REASON_TITLE),
            () -> assertEquals(appeal.getReason().getOther().getDescription(), TEST_REASON_DESCRIPTION));
    }

    @Test
    public void testGetAppealById_returnsEmptyAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Appeal> appealOpt = appealService.getAppeal(TEST_RESOURCE_ID);

        assertFalse(appealOpt.isPresent());
    }
}
