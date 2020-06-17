package uk.gov.companieshouse.model;

import java.util.Objects;

public class CreatedBy {
    
    private String id;

    public CreatedBy() {
        this(null);
    }

    public CreatedBy(String id) {
        this.id = id;
    }

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
