package uk.gov.companieshouse.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OtherReason {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "description must not be blank")
    private String description;
}
