package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.Appeal;

@Component
public class AppealMapper implements Mapper<AppealEntity, Appeal> {

    private final CreatedByMapper createdByMapper;
    private final PenaltyIdentifierMapper penaltyIdentifierMapper;
    private final ReasonMapper reasonMapper;

    public AppealMapper(CreatedByMapper createdByMapper, PenaltyIdentifierMapper penaltyIdentifierMapper, ReasonMapper reasonMapper) {
        this.createdByMapper = createdByMapper;
        this.penaltyIdentifierMapper = penaltyIdentifierMapper;
        this.reasonMapper = reasonMapper;
    }

    @Override
    public AppealEntity map(Appeal value) {
        if (value == null) {
            return null;
        }
        return new AppealEntity(
            value.getId(),
            value.getCreatedAt(),
            createdByMapper.map(value.getCreatedBy()),
            penaltyIdentifierMapper.map(value.getPenaltyIdentifier()),
            reasonMapper.map(value.getReason())
        );
    }

    @Override
    public Appeal map(AppealEntity value) {
        if (value == null) {
            return null;
        }
        return new Appeal(
            value.getId(),
            value.getCreatedAt(),
            createdByMapper.map(value.getCreatedBy()),
            penaltyIdentifierMapper.map(value.getPenaltyIdentifier()),
            reasonMapper.map(value.getReason())
        );
    }
}
