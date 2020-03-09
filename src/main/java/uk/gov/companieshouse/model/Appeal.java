package uk.gov.companieshouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Document(collection = "appeals")
public class Appeal {

    @JsonIgnore
    @Id
    private String _id;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private String userId;

    @Valid
    @NotNull(message = "penaltyIdentifier must not be null")
    private PenaltyIdentifier penaltyIdentifier;

    @Valid
    @NotNull(message = "reasons must not be null")
    @JsonProperty("reasons")
    private Reason reason;
}
