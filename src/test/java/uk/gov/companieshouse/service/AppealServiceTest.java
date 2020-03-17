package uk.gov.companieshouse.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.exception.AppealNotFoundException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AppealServiceTest {

    private static final String TEST_COMPANY_ID = "12345678";
    private static final String TEST_RESOURCE_ID = "1";
    private static final String TEST_PENALTY_REFERENCE = "A12345678";
    private static final String TEST_REASON_TITLE = "This is a title";
    private static final String TEST_REASON_DESCRIPTION = "This is a description";
    private static final String TEST_ERIC_ID = "1";

    @InjectMocks
    private AppealService appealService;

    @Mock
    private AppealRepository appealRepository;

    @Test
    public void testCreateAppeal_returnsResourceId() throws Exception {

        Appeal appeal = createAppeal();
        Appeal persistedAppeal = createAppeal();

        persistedAppeal.setCreatedBy(new CreatedBy(TEST_ERIC_ID));
        persistedAppeal.setId(TEST_RESOURCE_ID);
        when(appealRepository.insert(any(Appeal.class))).thenReturn(persistedAppeal);

        String resourceId = appealService.saveAppeal(appeal, TEST_ERIC_ID);

        Assertions.assertAll("Create appeal returns resource id",
            () -> assertNotNull(resourceId),
            () -> assertEquals(resourceId, TEST_RESOURCE_ID));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfNoResourceIdReturned() {

        Appeal persistedAppeal = createAppeal();
        persistedAppeal.setCreatedBy(new CreatedBy(TEST_ERIC_ID));
        persistedAppeal.setId(null);

        when(appealRepository.insert(any(Appeal.class))).thenReturn(persistedAppeal);

        Assertions.assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(), TEST_ERIC_ID));
    }

    @Test
    public void testCreateAppeal_throwsExceptionIfUnableToInsertData() {

        when(appealRepository.insert(any(Appeal.class))).thenReturn(null);

        Assertions.assertThrows(Exception.class, () -> appealService.saveAppeal(createAppeal(), TEST_ERIC_ID));
    }

    @Test
    public void testGetAppealById_returnsAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(java.util.Optional.of(createAppeal()));

        Appeal appeal = appealService.getAppeal(TEST_RESOURCE_ID);

        assertNotNull(appeal);
    }

    @Test
    public void testGetAppealById_throwsExceptionIfUnableToFindAppeal() {

        when(appealRepository.findById(any(String.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(AppealNotFoundException.class, () -> appealService.getAppeal(TEST_RESOURCE_ID));
    }

    private Appeal createAppeal() {

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
}
