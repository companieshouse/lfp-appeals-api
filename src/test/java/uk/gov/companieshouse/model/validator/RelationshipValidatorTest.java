package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
public class RelationshipValidatorTest {

    @InjectMocks
    RelationshipValidator relationshipValidator;

    private Appeal createTestIllnessAppeal() {
        CreatedBy mockCreated = TestUtil.buildCreatedBy();
        Reason reason = new Reason();
        reason.setIllness(TestUtil.createIllnessReason());
        Appeal mockAppeal = TestUtil.createAppeal(mockCreated, reason);

        return mockAppeal;
    }

    private Appeal createTestOtherAppeal() {
        CreatedBy mockCreated = TestUtil.buildCreatedBy();
        Reason reason = new Reason();
        reason.setOther(TestUtil.createOtherReason());
        Appeal mockAppeal = TestUtil.createAppeal(mockCreated, reason);

        return mockAppeal;
    }

    @Test
    void shouldMakeRelationshipNullWhenRelationshipAndIllnessExist(){
        Appeal mockAppeal = createTestIllnessAppeal();
        relationshipValidator.validateRelationship(mockAppeal);
        assertNull(mockAppeal.getCreatedBy().getRelationshipToCompany());
    }

    @Test
    void shouldReturnStringIfRelationshipIsNullWithOtherReason(){
        Appeal mockAppeal = createTestOtherAppeal();
        mockAppeal.getCreatedBy().setRelationshipToCompany(null);
        assertEquals(TestData.RELATIONSHIP_ERROR_MESSAGE,
            relationshipValidator.validateRelationship(mockAppeal));
    }

    @Test
    void shouldReturnNullIfRelationshipIsNotNullWithOtherReason(){
        Appeal mockAppeal = createTestOtherAppeal();
        assertNull(relationshipValidator.validateRelationship(mockAppeal));
    }

    @Test
    void shouldReturnNullIfRelationshipIsNullWithIllnessReason(){
        Appeal mockAppeal = createTestIllnessAppeal();
        assertNull(relationshipValidator.validateRelationship(mockAppeal));
    }

}
