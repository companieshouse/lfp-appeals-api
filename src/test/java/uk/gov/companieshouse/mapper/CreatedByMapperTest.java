package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.model.CreatedBy;

@ExtendWith(SpringExtension.class)
public class CreatedByMapperTest {
    private final CreatedByMapper mapper = new CreatedByMapper();

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((CreatedBy) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            CreatedByEntity mapped = mapper.map(new CreatedBy(TestData.USER_ID, TestData.EMAIL));
            assertEquals(TestData.USER_ID, mapped.getId());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((CreatedByEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            CreatedBy mapped = mapper.map(new CreatedByEntity(TestData.USER_ID));
            assertEquals(TestData.USER_ID, mapped.getId());
            assertNull(mapped.getEmailAddress());
        }
    }
}
