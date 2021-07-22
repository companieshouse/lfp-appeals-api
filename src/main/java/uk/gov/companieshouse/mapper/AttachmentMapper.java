package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.Attachment;

@Component
public class AttachmentMapper implements Mapper<AttachmentEntity, Attachment> {

    @Override
    public AttachmentEntity map(Attachment value) {
        if (value == null) {
            return null;
        }

        AttachmentEntity attachmentEntity = new AttachmentEntity();
        attachmentEntity.setId(value.getId());
        attachmentEntity.setName(value.getName());
        attachmentEntity.setContentType(value.getContentType());
        attachmentEntity.setSize(value.getSize());
        return attachmentEntity;
    }

    @Override
    public Attachment map(AttachmentEntity value) {
        if (value == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setId(value.getId());
        attachment.setName(value.getName());
        attachment.setContentType(value.getContentType());
        attachment.setSize(value.getSize());
        return attachment;
    }
}
