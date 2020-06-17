package uk.gov.companieshouse.mapper;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.mapper.base.Mapper;
import uk.gov.companieshouse.model.CreatedBy;

@Component
public class CreatedByMapper implements Mapper<CreatedByEntity, CreatedBy> {

    @Override
    public CreatedByEntity map(CreatedBy value) {
        if (value == null) {
            return null;
        }
        return new CreatedByEntity(value.getId());
    }

    @Override
    public CreatedBy map(CreatedByEntity value) {
        if (value == null) {
            return null;
        }
        return new CreatedBy(value.getId(), null);
    }
}
