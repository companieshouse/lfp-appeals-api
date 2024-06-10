package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;

class RelationshipValidatorTest {

    private RelationshipValidator relationshipValidator;

    @BeforeEach
    void setup() {
        relationshipValidator = new RelationshipValidator();
    }

    private Appeal createTestIllnessAppeal() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = new Reason();
        reason.setIllness(TestUtil.createIllnessReason());
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);

        return appeal;
    }

    private Appeal createTestOtherAppeal() {
        CreatedBy createdBy = TestUtil.buildCreatedBy();
        Reason reason = new Reason();
        reason.setOther(TestUtil.createOtherReason());
        Appeal appeal = TestUtil.createAppeal(createdBy, reason);

        return appeal;
    }

    @DisplayName("Should return string if relationship is null with other reason")
    @Test
    void shouldReturnStringIfRelationshipIsNullWithOtherReason(){
        Appeal appeal = createTestOtherAppeal();
        appeal.getCreatedBy().setRelationshipToCompany(null);
        assertEquals(TestData.RELATIONSHIP_ERROR_MESSAGE,
            relationshipValidator.validateRelationship(appeal));
    }

    @DisplayName("Should return null if relationship is not null with other reason")
    @Test
    void shouldReturnNullIfRelationshipIsNotNullWithOtherReason(){
        Appeal appeal = createTestOtherAppeal();
        assertNull(relationshipValidator.validateRelationship(appeal));
    }

    @DisplayName("Should return null if relationship is null with illness reason")
    @Test
    void shouldReturnNullIfRelationshipIsNullWithIllnessReason(){
        Appeal appeal = createTestIllnessAppeal();
        assertNull(relationshipValidator.validateRelationship(appeal));
    }

}
