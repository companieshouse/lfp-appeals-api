package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.Reason;

@Component
public class ReasonMapper implements Mapper<ReasonEntity, Reason> {

    private final OtherReasonMapper otherReasonMapper;

    public ReasonMapper(OtherReasonMapper otherReasonMapper) {
        this.otherReasonMapper = otherReasonMapper;
    }

    @Override
    public ReasonEntity map(Reason value) {
        if (value == null) {
            return null;
        }
        return new ReasonEntity(otherReasonMapper.map(value.getOther()));
    }

    @Override
    public Reason map(ReasonEntity value) {
        if (value == null) {
            return null;
        }
        return new Reason(otherReasonMapper.map(value.getOther()));
    }
}
