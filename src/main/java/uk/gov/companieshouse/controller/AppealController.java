package uk.gov.companieshouse.controller;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.service.AppealService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/companies")
public class AppealController {

    public final AppealService appealService;

    @Operation(summary = "Create a new appeal", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appeal resource created"),
        @ApiResponse(responseCode = "401", description = "Unauthorised request"),
        @ApiResponse(responseCode = "422", description = "Invalid appeal data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/{company-id}/appeals", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity submitAppeal(@RequestHeader("ERIC-identity") String ericIdentity,
                                       @PathVariable("company-id") final String companyId,
                                       @Valid @RequestBody final Appeal appeal) {

        log.info("POST /companies/{}/appeals with user id {} and appeal data {}",
            companyId, ericIdentity, Json.pretty(appeal));

        if (StringUtils.isBlank(ericIdentity)) {

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        }

        try {

            String id = appealService.saveAppeal(companyId, appeal, ericIdentity);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, id) // this should be updated with URI, i.e. /appeals/{id}
                .build();

        } catch (Exception ex) {

            log.error("Unable to create appeal for company number {} and user id {}", companyId, ericIdentity, ex);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    @Operation(summary = "Get an appeal by ID", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appeal resource retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Appeal not found")
    })
    @GetMapping(value = "/{company-id}/appeals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appeal> getAppealById(@PathVariable("company-id") final String companyId,
                                                @PathVariable("id") final String id) {

        log.info("GET /companies/{}/appeals/{}", companyId, id);

        try {

            Appeal appeal = appealService.getAppeal(id);

            return new ResponseEntity(appeal, HttpStatus.OK);

        } catch (Exception ex) {

            log.error("Unable to get appeal for company number {} and id {}", companyId, id, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }

}
