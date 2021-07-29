package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.TestUtil;

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
            AttachmentEntity mapped = mapper.map(TestUtil.createAttachment());

            assertEquals(TestData.ATTACHMENT_ID, mapped.getId());
            assertEquals(TestData.ATTACHMENT_NAME, mapped.getName());
            assertEquals(TestData.CONTENT_TYPE, mapped.getContentType());
            assertEquals(TestData.ATTACHMENT_SIZE, mapped.getSize());
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
            Attachment mapped = mapper.map(TestUtil.createAttachmentEntity());
            assertEquals(TestData.ATTACHMENT_ID, mapped.getId());
            assertEquals(TestData.ATTACHMENT_NAME, mapped.getName());
            assertEquals(TestData.CONTENT_TYPE, mapped.getContentType());
            assertEquals(TestData.ATTACHMENT_SIZE, mapped.getSize());
            assertNull(mapped.getUrl());
        }
    }
}
