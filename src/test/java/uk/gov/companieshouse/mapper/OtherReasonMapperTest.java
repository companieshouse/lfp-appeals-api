package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.TestUtil;

@ExtendWith(MockitoExtension.class)
class OtherReasonMapperTest {

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private Attachment mockAttachment;

    @Mock
    private AttachmentEntity mockAttachmentEntity;

    @InjectMocks
    private OtherReasonMapper mapper;

    @Nested
    class ToEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((OtherReason) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {

            OtherReason otherReason = TestUtil.createOtherReason();

            when(attachmentMapper.map(otherReason.getAttachments().get(0))).thenReturn(mockAttachmentEntity);

            OtherReasonEntity mapped = mapper.map(otherReason);

            assertEquals(TestData.TITLE, mapped.getTitle());
            assertEquals(TestData.DESCRIPTION, mapped.getDescription());
            assertEquals(mockAttachmentEntity, mapped.getAttachments().get(0));

            verify(attachmentMapper).map(otherReason.getAttachments().get(0));
        }
    }

    @Nested
    class FromEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((OtherReasonEntity) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {

           OtherReasonEntity entity = TestUtil.createOtherReasonEntity();

            when(attachmentMapper.map(entity.getAttachments().get(0))).thenReturn(mockAttachment);

            OtherReason mapped = mapper.map(entity);

            assertEquals(TestData.TITLE, mapped.getTitle());
            assertEquals(TestData.DESCRIPTION, mapped.getDescription());
            assertEquals(mockAttachment, mapped.getAttachments().get(0));

            verify(attachmentMapper).map(entity.getAttachments().get(0));
        }
    }
}
