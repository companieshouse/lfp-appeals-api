package uk.gov.companieshouse.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.mapper.base.Mapper;

import java.util.stream.Collectors;

@Component
public class IllnessReasonMapper implements Mapper<IllnessReasonEntity, IllnessReason> {

    @Autowired
    private AttachmentMapper attachmentMapper;

    public IllnessReasonEntity map(IllnessReason value) {
        if (value == null) {
            return null;
        }
        return new IllnessReasonEntity(
            value.getIllPerson(),
            value.getOtherPerson(),
            value.getIllnessStart(),
            value.getContinuedIllness(),
            value.getIllnessEnd(),
            value.getIllnessImpactFurtherInformation(),
            value.getAttachments() != null ? value.getAttachments().stream()
                .map(attachmentMapper::map)
                .collect(Collectors.toList()) : null
        );
    }

    public IllnessReason map(IllnessReasonEntity value) {
        if (value == null) {
            return null;
        }
        return new IllnessReason(
            value.getIllPerson(),
            value.getOtherPerson(),
            value.getIllnessStartDate(),
            value.getContinuedIllness(),
            value.getIllnessEndDate(),
            value.getIllnessImpactFurtherInformation(),
            value.getAttachments() != null ? value.getAttachments().stream()
                .map(attachmentMapper::map)
                .collect(Collectors.toList()) : null
        );
    }

}
