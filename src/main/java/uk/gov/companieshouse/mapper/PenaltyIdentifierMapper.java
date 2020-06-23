package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.exception.EntityMappingException;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.service.CompanyNumberRegexFactory;

@Component
public class PenaltyIdentifierMapper implements Mapper<PenaltyIdentifierEntity, PenaltyIdentifier> {

    private CompanyNumberRegexFactory companyNumberRegexFactory;

    public PenaltyIdentifierMapper(CompanyNumberRegexFactory companyNumberRegexFactory) {
        this.companyNumberRegexFactory = companyNumberRegexFactory;
    }

    @Override
    public PenaltyIdentifierEntity map(PenaltyIdentifier value) throws EntityMappingException {
        if (value == null) {
            return null;
        }

        if (!this.companyNumberRegexFactory.matchCompanyNumber(value.getCompanyNumber())) {
            throw new EntityMappingException("Company Number prefix is not permitted");
        }

        return new PenaltyIdentifierEntity(value.getCompanyNumber(), value.getPenaltyReference());
    }

    @Override
    public PenaltyIdentifier map(PenaltyIdentifierEntity value) {
        if (value == null) {
            return null;
        }
        return new PenaltyIdentifier(value.getCompanyNumber(), value.getPenaltyReference());
    }
}
