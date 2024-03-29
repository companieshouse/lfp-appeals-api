package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.model.PenaltyIdentifier;

@ExtendWith(MockitoExtension.class)
class PenaltyIdentifierMapperTest {

    private final PenaltyIdentifierMapper mapper = new PenaltyIdentifierMapper();

    @Nested
    class ToEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((PenaltyIdentifier) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            PenaltyIdentifierEntity mapped = mapper.map(new PenaltyIdentifier(TestData.COMPANY_NUMBER, TestData.PENALTY_REFERENCE));
            assertEquals(TestData.COMPANY_NUMBER, mapped.getCompanyNumber());
            assertEquals(TestData.PENALTY_REFERENCE, mapped.getPenaltyReference());
        }
    }

    @Nested
    class FromEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((PenaltyIdentifierEntity) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            PenaltyIdentifier mapped = mapper.map(new PenaltyIdentifierEntity(TestData.COMPANY_NUMBER, TestData.PENALTY_REFERENCE));
            assertEquals(TestData.COMPANY_NUMBER, mapped.getCompanyNumber());
            assertEquals(TestData.PENALTY_REFERENCE, mapped.getPenaltyReference());
        }
    }
}
