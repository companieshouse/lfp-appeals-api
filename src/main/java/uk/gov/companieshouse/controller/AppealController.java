package uk.gov.companieshouse.controller;

import io.swagger.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.service.AppealService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/appeals")
public class AppealController {

    public final AppealService appealService;

    @Operation(summary = "Create a new appeal", tags = "Appeal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appeal resource created"),
        @ApiResponse(responseCode = "500", description = "Internal server error"),
        @ApiResponse(responseCode = "422", description = "Invalid appeal data")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitAppeal(@RequestHeader("Eric-identity") final String userId, @RequestBody @Valid final Appeal appeal) {

        log.info("POST /appeals with id {} and appeal data {}", userId, Json.pretty(appeal));

        String id = appealService.createAppeal(userId, appeal);

        HttpHeaders headers = new HttpHeaders();
        headers.add("resource_id", id);

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
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
