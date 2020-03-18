package uk.gov.companieshouse.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;

import java.io.File;

public class IntegrationTestUtil {

    public static final String TEST_COMPANY_ID = "12345678";
    public static final String TEST_PENALTY_REFERENCE = "A12345678";
    public static final String TEST_REASON_TITLE = "This is a title";
    public static final String TEST_REASON_DESCRIPTION = "This is a description";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String asJsonString(String pathname) {
        try {
            Appeal appeal = MAPPER.readValue(new File(pathname), Appeal.class);
            return new ObjectMapper().writeValueAsString(appeal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Appeal getValidAppeal() {

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
