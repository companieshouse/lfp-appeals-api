package uk.gov.companieshouse.controller;

import io.swagger.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.service.AppealService;
import uk.gov.companieshouse.service.ServiceException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/companies")
public class AppealController {

    public static final String RESOURCE_ID_HEADER = "resource_id";
    public final AppealService appealService;

    @Operation(summary = "Create a new appeal", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appeal resource created"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "422", description = "Invalid appeal data")
    })
    @PostMapping(value = "/{company-id}/appeals", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitAppeal(@RequestHeader("Eric-identity") final String userId,
                                               @PathVariable("company-id") final String companyId,
                                               @Valid @RequestBody final Appeal appeal) {

        log.info("POST /companies/{}/appeals with user id {} and appeal data {}",
            companyId, userId, Json.pretty(appeal));

        try {

            String resourceId = appealService.createAppeal(userId, companyId, appeal);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(RESOURCE_ID_HEADER, resourceId)
                .build();

        } catch (ServiceException e) {

            log.error("Unable to create appeal", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
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
