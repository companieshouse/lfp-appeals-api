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
        CreatedByEntity createdByEntity = new CreatedByEntity();
        createdByEntity.setId(value.getId());
        return createdByEntity;
    }

    @Override
    public CreatedBy map(CreatedByEntity value) {
        if (value == null) {
            return null;
        }
        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(value.getId());
        return createdBy;
    }
}
