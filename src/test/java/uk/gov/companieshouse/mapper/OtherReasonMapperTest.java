package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.OtherReason;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.description;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.title;

@ExtendWith(SpringExtension.class)
public class OtherReasonMapperTest {

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
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((OtherReason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            List<Attachment> attachments = new ArrayList<Attachment>();
            attachments.add(mockAttachment);

            OtherReasonEntity mapped = mapper.map(new OtherReason(title, description, attachments));

            assertEquals(title, mapped.getTitle());
            assertEquals(description, mapped.getDescription());
            assertEquals(mockAttachment, attachments.get(0));

            verify(attachmentMapper).map(attachments.get(0));
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
            List<AttachmentEntity> attachmentEntities = new ArrayList<AttachmentEntity>();
            attachmentEntities.add(mockAttachmentEntity);

            OtherReason mapped = mapper.map(new OtherReasonEntity(title, description, attachmentEntities));

            assertEquals(title, mapped.getTitle());
            assertEquals(description, mapped.getDescription());
            assertEquals(mockAttachmentEntity, attachmentEntities.get(0));

            verify(attachmentMapper).map(attachmentEntities.get(0));
        }
    }
}
