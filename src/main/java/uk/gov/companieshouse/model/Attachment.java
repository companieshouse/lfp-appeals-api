package uk.gov.companieshouse.model;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Attachment {

    @NotBlank(message = "attachement id must not be null.")
    private String id;

    @NotBlank(message = "name must not be blank.")
    private String name;

    @NotBlank(message = "contentType must not be blank.")
    private String contentType;

    @NotNull()
    private Integer size;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public int getSize() {
        return size;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Attachment that = (Attachment) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contentType, size);
    }

    @Override
    public String toString() {
        return "Attachment{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", contentType='" + contentType + '\''
                + ", size='" + size + '\'' + '}';
    }

}
