package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.TestUtil;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.model.CreatedBy;

@ExtendWith(MockitoExtension.class)
class CreatedByMapperTest {
    private final CreatedByMapper mapper = new CreatedByMapper();

    @Nested
    class ToEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((CreatedBy) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            CreatedBy createdBy = TestUtil.buildCreatedBy();
            CreatedByEntity mapped = mapper.map(createdBy);
            assertEquals(TestData.USER_ID, mapped.getId());
        }
    }

    @Nested
    class FromEntityMappingTest {

        @DisplayName("Should return null when value is not null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((CreatedByEntity) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            CreatedByEntity createdByEntity = new CreatedByEntity();
            createdByEntity.setId(TestData.USER_ID);
            CreatedBy mapped = mapper.map(createdByEntity);
            assertEquals(TestData.USER_ID, mapped.getId());
            assertNull(mapped.getEmailAddress());
        }
    }
}
