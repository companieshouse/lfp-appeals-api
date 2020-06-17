package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.model.CreatedBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestData.Appeal.CreatedBy.id;

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
            CreatedByEntity mapped = mapper.map(new CreatedBy(id));
            assertEquals(id, mapped.getId());
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
            CreatedBy mapped = mapper.map(new CreatedByEntity(id));
            assertEquals(id, mapped.getId());
        }
    }
}
