package uk.gov.companieshouse.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.ChipsContact;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;

class ChipsContactDescriptionFormatterTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ChipsContactDescriptionFormatter formatter;

    @BeforeEach
    void setUp(){
        formatter = new ChipsContactDescriptionFormatter();
    }

    @Test
    void testBuildChipsContactOtherReasonWithAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = TestUtil.createReasonWithOther();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionOtherReasonWithAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactOtherReasonEmptyAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = TestUtil.createReasonWithOther();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getOther().setAttachments(Collections.emptyList());

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactOtherReasonsNullAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = TestUtil.createReasonWithOther();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getOther().setAttachments(null);

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionWithoutAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactIllnessReasonWithAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = createReasonWithIllness();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactIllnessReasonWithoutAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = createReasonWithIllness();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getIllness().setAttachments(Collections.emptyList());

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithoutAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactIllnessReasonWithNullAttachments() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = createReasonWithIllness();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getIllness().setAttachments(null);

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithoutAttachments(), contactDescription);
    }

    @Test
    void testBuildChipsContactIllnessReasonWithoutEndDate() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = createReasonWithIllness();
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);
        appeal.setId(TestData.ID);
        appeal.getReason().getIllness().setAttachments(null);
        appeal.getReason().getIllness().setContinuedIllness(true);

        ChipsContact chipsContact = formatter.buildChipsContact(appeal);

        assertEquals(appeal.getPenaltyIdentifier().getCompanyNumber(), chipsContact.getCompanyNumber());
        assertEquals(appeal.getCreatedAt().format(DATE_TIME_FORMATTER), chipsContact.getDateReceived());
        String contactDescription = chipsContact.getContactDescription();
        assertEquals(expectedContactDescriptionIllnessReasonWithContinuedIllness(), contactDescription);
    }

    private Reason createReasonWithIllness(){
        Reason reason = new Reason();
        reason.setIllness(TestUtil.createIllnessReason());
        return reason;
    }

    private String expectedContactDescriptionIllnessReasonWithAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nName of User: "
            + TestData.YOUR_NAME
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nIll Person: "
            + TestData.ILL_PERSON
            + "\nOther Person: "
            + TestData.OTHER_PERSON
            + "\nIllness Start Date: "
            + TestData.ILLNESS_START
            + "\nContinued Illness: "
            + TestData.CONTINUED_ILLNESS
            + "\nIllness End Date: "
            + TestData.ILLNESS_END
            + "\nFurther information: "
            + TestData.ILLNESS_IMPACT_FURTHER_INFORMATION
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.ATTACHMENT_NAME
            + "\n    "
            + TestData.ATTACHMENT_URL
            + "&a="
            + TestData.ID;
    }

    private String expectedContactDescriptionWithoutAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nName of User: "
            + TestData.YOUR_NAME
            + "\nRelationship to Company: "
            + TestData.RELATIONSHIP
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.TITLE
            + "\nFurther information: "
            + TestData.DESCRIPTION
            + "\nSupporting documents: None";
    }

    private String expectedContactDescriptionOtherReasonWithAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nName of User: "
            + TestData.YOUR_NAME
            + "\nRelationship to Company: "
            + TestData.RELATIONSHIP
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nReason: "
            + TestData.TITLE
            + "\nFurther information: "
            + TestData.DESCRIPTION
            + "\nSupporting documents: "
            + "\n  - "
            + TestData.ATTACHMENT_NAME
            + "\n    "
            + TestData.ATTACHMENT_URL
            + "&a="
            + TestData.ID;
    }

    private String expectedContactDescriptionIllnessReasonWithoutAttachments() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nName of User: "
            + TestData.YOUR_NAME
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nIll Person: "
            + TestData.ILL_PERSON
            + "\nOther Person: "
            + TestData.OTHER_PERSON
            + "\nIllness Start Date: "
            + TestData.ILLNESS_START
            + "\nContinued Illness: "
            + TestData.CONTINUED_ILLNESS
            + "\nIllness End Date: "
            + TestData.ILLNESS_END
            + "\nFurther information: "
            + TestData.ILLNESS_IMPACT_FURTHER_INFORMATION
            + "\nSupporting documents: None";
    }

    private String expectedContactDescriptionIllnessReasonWithContinuedIllness() {
        return "Appeal submitted"
            + "\n\nYour reference number is your company number "
            + TestData.COMPANY_NUMBER
            + "\n\nCompany Number: "
            + TestData.COMPANY_NUMBER
            + "\nName of User: "
            + TestData.YOUR_NAME
            + "\nEmail address: "
            + TestData.EMAIL
            + "\n\nAppeal Reason"
            + "\nIll Person: "
            + TestData.ILL_PERSON
            + "\nOther Person: "
            + TestData.OTHER_PERSON
            + "\nIllness Start Date: "
            + TestData.ILLNESS_START
            + "\nContinued Illness: "
            + true
            + "\nFurther information: "
            + TestData.ILLNESS_IMPACT_FURTHER_INFORMATION
            + "\nSupporting documents: None";
    }
}
