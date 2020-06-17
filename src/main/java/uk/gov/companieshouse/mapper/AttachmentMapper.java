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
        return new AttachmentEntity(
            value.getId(),
            value.getName(),
            value.getContentType(),
            value.getSize()
        );
    }

    @Override
    public Attachment map(AttachmentEntity value) {
        if (value == null) {
            return null;
        }
        return new Attachment(
            value.getId(),
            value.getName(),
            value.getContentType(),
            value.getSize()
        );
    }
}
