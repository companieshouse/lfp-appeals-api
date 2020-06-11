package uk.gov.companieshouse.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.data.annotation.Transient;

public class CreatedBy {
    
    @JsonAlias({"_id"})
    private String id;

    @Transient
    private String emailAddress;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatedBy createdBy = (CreatedBy) o;
        return Objects.equals(id, createdBy.id) &&
            Objects.equals(emailAddress, createdBy.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress);
    }

    @Override
    public String toString() {
        return "CreatedBy{" +
            "id='" + id + '\'' +
            ", emailAddress='" + emailAddress + '\'' +
            '}';
    }
}
