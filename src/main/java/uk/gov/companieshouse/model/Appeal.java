package uk.gov.companieshouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document(collection = "appeals")
public class Appeal {

    @JsonIgnore
    @Id
    private String id;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private CreatedBy createdBy;

    @Valid
    @NotNull(message = "penaltyIdentifier must not be null")
    private PenaltyIdentifier penaltyIdentifier;

    @Valid
    @NotNull(message = "reasons must not be null")
    @JsonProperty("reasons")
    private Reason reason;
}
