package uk.gov.companieshouse.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.OtherReason;

import java.util.stream.Collectors;

@Component
public class OtherReasonMapper implements Mapper<OtherReasonEntity, OtherReason> {

    @Autowired
    private AttachmentMapper attachmentMapper;

    public OtherReasonEntity map(OtherReason value) {
        if (value == null) {
            return null;
        }
        return new OtherReasonEntity(
            value.getTitle(),
            value.getDescription(),
            value.getAttachments() != null ? value.getAttachments().stream()
                .map(attachmentMapper::map)
                .collect(Collectors.toList()) : null
        );
    }

    public OtherReason map(OtherReasonEntity value) {
        if (value == null) {
            return null;
        }
        return new OtherReason(
            value.getTitle(),
            value.getDescription(),
            value.getAttachments() != null ? value.getAttachments().stream()
                .map(attachmentMapper::map)
                .collect(Collectors.toList()) : null
        );
    }

}
