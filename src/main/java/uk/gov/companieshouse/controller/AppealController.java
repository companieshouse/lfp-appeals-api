package uk.gov.companieshouse.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.companieshouse.exception.AppealReasonException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.validator.AppealReasonValidator;
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

    @Autowired
    private AppealReasonValidator appealReasonValidator;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

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
            appealReasonValidator.validate(appeal.getReason());
            final String id = appealService.saveAppeal(appeal, userId);

            final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

            return ResponseEntity.created(location).body(id);

        } catch (AppealReasonException appealReasonException) {
            LOGGER.error(appealReasonException.getMessage() +
                " for company number {}, penalty reference {} and user id {}",
                    companyId, penaltyReference, userId, appealReasonException);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            LOGGER.error("Unable to create appeal for company number {}, penalty reference {} and user id {}",
                companyId, penaltyReference, userId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{company-id}/appeals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appeal> getAppealById(@PathVariable("company-id") final String companyId,
                                                @PathVariable("id") final String id) {

        LOGGER.info("GET /companies/{}/appeals/{}", companyId, id);

        final Optional<Appeal> appeal = appealService.getAppeal(id);

        return appeal.map(a -> ResponseEntity.status(HttpStatus.OK).body(a))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(value = "/{company-id}/appeals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appeal>> getAppealsByPenaltyReference(@PathVariable("company-id") final String companyId,
                                                              @RequestParam(value="penaltyReference") final String penaltyReference) {

        LOGGER.info("GET /companies/{}/appeals?penaltyReference={}", companyId, penaltyReference);

        final List<Appeal> appealList = appealService.getAppealsByPenaltyReference(penaltyReference);

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
