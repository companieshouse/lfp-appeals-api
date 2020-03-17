package uk.gov.companieshouse.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.companieshouse.exception.AppealNotFoundException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;
import uk.gov.companieshouse.repository.AppealRepository;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
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

        assertThat(resourceId, is(notNullValue()));
        assertThat(resourceId, is(TEST_RESOURCE_ID));
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

        assertThat(appeal, is(notNullValue()));
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
