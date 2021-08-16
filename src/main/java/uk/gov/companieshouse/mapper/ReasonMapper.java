package uk.gov.companieshouse.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.Reason;

@Component
public class ReasonMapper implements Mapper<ReasonEntity, Reason> {

    @Autowired
    private OtherReasonMapper otherReasonMapper;

    @Autowired
    private IllnessReasonMapper illnessReasonMapper;

    @Override
    public ReasonEntity map(Reason value) {
        if (value == null) {
            return null;
        }

        ReasonEntity reasonEntity = new ReasonEntity();
        reasonEntity.setIllnessReason(illnessReasonMapper.map(value.getIllness()));
        reasonEntity.setOther(otherReasonMapper.map(value.getOther()));

        return reasonEntity;
    }

    @Override
    public Reason map(ReasonEntity value) {
        if (value == null) {
            return null;
        }

        Reason reason = new Reason();
        reason.setIllness(illnessReasonMapper.map(value.getIllnessReason()));
        reason.setOther(otherReasonMapper.map(value.getOther()));

        return reason;
    }
}
