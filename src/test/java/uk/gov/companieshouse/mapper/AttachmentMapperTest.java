package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.TestUtil;

@ExtendWith(MockitoExtension.class)
class AttachmentMapperTest {
    private final AttachmentMapper mapper = new AttachmentMapper();

    @Nested
    class ToEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Attachment) null));
        }

        @DisplayName("Should map value when value is not null")
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

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((AttachmentEntity) null));
        }

        @DisplayName("Should map value when value is not null")
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
