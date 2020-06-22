package uk.gov.companieshouse.model;

import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Attachment {

    @NotBlank(message = "attachment id must not be blank")
    private String id;

    @NotBlank(message = "attachment name must not be blank")
    private String name;

    @NotBlank(message = "attachment contentType must not be blank")
    private String contentType;

    @NotNull(message = "attachment size must not be null")
    @Min(value = 1, message = "attachment size must be greater than 0 bytes")
    private Integer size;

    private String url;

    public Attachment() {
        this(null, null, null, null, null);
    }

    public Attachment(String id, String name, String contentType, Integer size, String url) {
        this.id = id;
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getSize() {
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

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(contentType, that.contentType) &&
            Objects.equals(size, that.size) &&
            Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, contentType, size, url);
    }

    @Override
    public String toString() {
        return "Attachment{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", contentType='" + contentType + '\'' +
            ", size=" + size +
            ", url='" + url + '\'' +
            '}';
    }

}
