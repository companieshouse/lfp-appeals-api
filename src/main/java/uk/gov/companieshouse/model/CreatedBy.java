package uk.gov.companieshouse.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CreatedBy {
    
    @JsonAlias({"_id"})
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatedBy createdBy = (CreatedBy) o;
        return Objects.equals(id, createdBy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CreatedBy{" +
            "id='" + id + '\'' +
            '}';
    }
}
