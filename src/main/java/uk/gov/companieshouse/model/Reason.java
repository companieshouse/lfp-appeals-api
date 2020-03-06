package uk.gov.companieshouse.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class Reason {

    @Valid
    @NotNull(message = "other must not be null")
    private OtherReason other;
}
