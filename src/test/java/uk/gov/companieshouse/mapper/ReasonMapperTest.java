package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.Reason;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.description;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.title;

@ExtendWith(SpringExtension.class)
public class ReasonMapperTest {
    private final ReasonMapper mapper = new ReasonMapper(new OtherReasonMapper(new AttachmentMapper()));

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Reason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            ReasonEntity mapped = mapper.map(new Reason(new OtherReason(title, description, Collections.emptyList())));
            assertEquals(title, mapped.getOther().getTitle());
            assertEquals(description, mapped.getOther().getDescription());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((ReasonEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            Reason mapped = mapper.map(new ReasonEntity(new OtherReasonEntity(title, description, Collections.emptyList())));
            assertEquals(title, mapped.getOther().getTitle());
            assertEquals(description, mapped.getOther().getDescription());
        }
    }
}
