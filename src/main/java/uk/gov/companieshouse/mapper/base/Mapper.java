package uk.gov.companieshouse.mapper.base;

import java.io.Serializable;

public interface Mapper<Entity extends Serializable, Model> {
    Entity map(Model value);

    Model map(Entity value);
}
