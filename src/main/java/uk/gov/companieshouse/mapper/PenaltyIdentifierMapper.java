package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.PenaltyIdentifier;

@Component
public class PenaltyIdentifierMapper implements Mapper<PenaltyIdentifierEntity, PenaltyIdentifier> {

    @Override
    public PenaltyIdentifierEntity map(PenaltyIdentifier value) {
        if (value == null) {
            return null;
        }
        return new PenaltyIdentifierEntity(
            value.getCompanyNumber(),
            value.getPenaltyReference()
        );
    }

    @Override
    public PenaltyIdentifier map(PenaltyIdentifierEntity value) {
        if (value == null) {
            return null;
        }
        return new PenaltyIdentifier(
            value.getCompanyNumber(),
            value.getPenaltyReference()
        );
    }
}
