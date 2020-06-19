package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.model.Attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestData.Appeal.Reason.Attachment.contentType;
import static uk.gov.companieshouse.TestData.Appeal.Reason.Attachment.id;
import static uk.gov.companieshouse.TestData.Appeal.Reason.Attachment.name;
import static uk.gov.companieshouse.TestData.Appeal.Reason.Attachment.size;

@ExtendWith(SpringExtension.class)
public class AttachmentMapperTest {
    private final AttachmentMapper mapper = new AttachmentMapper();

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Attachment) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            AttachmentEntity mapped = mapper.map(new Attachment(id, name, contentType, size, null));
            assertEquals(id, mapped.getId());
            assertEquals(name, mapped.getName());
            assertEquals(contentType, mapped.getContentType());
            assertEquals(size, mapped.getSize());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((AttachmentEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            Attachment mapped = mapper.map(new AttachmentEntity(id, name, contentType, size));
            assertEquals(id, mapped.getId());
            assertEquals(name, mapped.getName());
            assertEquals(contentType, mapped.getContentType());
            assertEquals(size, mapped.getSize());
            assertNull(mapped.getUrl());
        }
    }
}
