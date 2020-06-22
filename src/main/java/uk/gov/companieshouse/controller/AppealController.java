package uk.gov.companieshouse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.service.AppealService;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/companies")
public class AppealController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealController.class);

    private final AppealService appealService;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    @Operation(summary = "Create a new appeal", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appeal resource created", headers = {
            @Header(name = "location")
        }),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorised request"),
        @ApiResponse(responseCode = "422", description = "Invalid appeal data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{company-id}/appeals", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitAppeal(@RequestHeader("ERIC-identity") String userId,
                                               @PathVariable("company-id") final String companyId,
                                               @Valid @RequestBody final Appeal appeal) {

        final String penaltyReference = appeal.getPenaltyIdentifier().getPenaltyReference();

        LOGGER.info("POST /companies/{}/appeals with user id {} and penalty reference {}",
            companyId, userId, penaltyReference);

        if (StringUtils.isBlank(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            final String id = appealService.saveAppeal(appeal, userId);

            final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

            return ResponseEntity.created(location).body(id);

        } catch (Exception ex) {
            LOGGER.error("Unable to create appeal for company number {}, penalty reference {} and user id {}",
                companyId, penaltyReference, userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get an appeal by ID", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appeal resource retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Appeal not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{company-id}/appeals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appeal> getAppealById(@PathVariable("company-id") final String companyId,
                                                @PathVariable("id") final String id) {

        LOGGER.info("GET /companies/{}/appeals/{}", companyId, id);

        final Optional<Appeal> appeal = appealService.getAppeal(id);

        return appeal.map(a -> ResponseEntity.status(HttpStatus.OK).body(a))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Get an appeal by penalty reference", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appeal resource retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Appeal not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{company-id}/appeals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appeal>> getAppealByPenaltyReference(@PathVariable("company-id") final String companyId,
                                                              @RequestParam(value="penaltyReference") final String penaltyReference) {

        LOGGER.info("GET /{}/appeals?penaltyReference={}", companyId, penaltyReference);

        final List<Appeal> appealList = appealService.getAppealByPenaltyReference(penaltyReference);

        if (appealList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(appealList);

    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }

}
