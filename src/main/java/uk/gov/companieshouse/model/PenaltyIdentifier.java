package uk.gov.companieshouse.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PenaltyIdentifier {

    @NotBlank(message = "companyNumber must not be blank")
    @Pattern(regexp = "(((SC|NI)[0-9]{1,6})|([0-9]{1,8}))",
        message = "companyNumber is invalid")
    private String companyNumber;

    @NotBlank(message = "penaltyReference must not be blank")
    @Pattern(regexp = "([A-Z]{1}[0-9]{8})",
        message = "penaltyReference is invalid")
    private String penaltyReference;
}
