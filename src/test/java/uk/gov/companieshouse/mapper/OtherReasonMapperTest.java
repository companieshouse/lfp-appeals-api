package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.model.OtherReason;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.description;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.title;

@ExtendWith(SpringExtension.class)
public class OtherReasonMapperTest {

    @Spy
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private OtherReasonMapper mapper;

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((OtherReason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            OtherReasonEntity mapped = mapper.map(new OtherReason(title, description, Collections.emptyList()));
            assertEquals(title, mapped.getTitle());
            assertEquals(description, mapped.getDescription());
            assertTrue(mapped.getAttachments().isEmpty());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((OtherReasonEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            OtherReason mapped = mapper.map(new OtherReasonEntity(title, description, Collections.emptyList()));
            assertEquals(title, mapped.getTitle());
            assertEquals(description, mapped.getDescription());
            assertTrue(mapped.getAttachments().isEmpty());
        }
    }
}
