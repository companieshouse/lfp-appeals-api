package uk.gov.companieshouse.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.mapper.base.Mapper;

import java.util.stream.Collectors;

@Component
public class IllnessReasonMapper implements Mapper<IllnessReasonEntity, IllnessReason> {


    private final AttachmentMapper attachmentMapper;

    @Autowired
    public IllnessReasonMapper(AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    public IllnessReasonEntity map(IllnessReason value) {
        if (value == null) {
            return null;
        }
        IllnessReasonEntity illnessReasonEntity = new IllnessReasonEntity();
        illnessReasonEntity.setIllPerson(value.getIllPerson());
        illnessReasonEntity.setOtherPerson(value.getOtherPerson());
        illnessReasonEntity.setIllnessStartDate(value.getIllnessStart());
        illnessReasonEntity.setContinuedIllness(value.getContinuedIllness());
        illnessReasonEntity.setIllnessEndDate(value.getIllnessEnd());
        illnessReasonEntity.setIllnessImpactFurtherInformation(value.getIllnessImpactFurtherInformation());
        illnessReasonEntity.setAttachments(value.getAttachments() != null ? value.getAttachments().stream()
            .map(attachmentMapper::map)
            .collect(Collectors.toList()) : null);
        return illnessReasonEntity;
    }

    public IllnessReason map(IllnessReasonEntity value) {
        if (value == null) {
            return null;
        }
        IllnessReason illnessReason = new IllnessReason();
        illnessReason.setIllPerson(value.getIllPerson());
        illnessReason.setOtherPerson(value.getOtherPerson());
        illnessReason.setIllnessStart(value.getIllnessStartDate());
        illnessReason.setContinuedIllness(value.getContinuedIllness());
        illnessReason.setIllnessEnd(value.getIllnessEndDate());
        illnessReason.setIllnessImpactFurtherInformation(value.getIllnessImpactFurtherInformation());
        illnessReason.setAttachments(value.getAttachments() != null ? value.getAttachments().stream()
            .map(attachmentMapper::map)
            .collect(Collectors.toList()) : null);
        return illnessReason;
    }

}
