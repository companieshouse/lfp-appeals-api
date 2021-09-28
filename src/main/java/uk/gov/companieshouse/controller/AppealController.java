package uk.gov.companieshouse.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.validator.AppealReasonValidator;
import uk.gov.companieshouse.model.validator.EndDateValidator;
import uk.gov.companieshouse.model.validator.RelationshipValidator;
import uk.gov.companieshouse.service.AppealService;

@RestController
@RequestMapping("/companies")
public class AppealController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);
    private final AppealService appealService;

    @Autowired
    private AppealReasonValidator appealReasonValidator;
    @Autowired
    private RelationshipValidator relationshipValidator;
    @Autowired
    private EndDateValidator endDateValidator;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    @PostMapping(value = "/{company-number}/appeals", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitAppeal(@RequestHeader("ERIC-identity") String userId,
                                               @PathVariable("company-number") final String companyId,
                                               @Valid @RequestBody final Appeal appeal) {

        if (StringUtils.isBlank(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String validationError = appealReasonValidator.validate(appeal.getReason());
        if (validationError != null) {
            LOGGER.info("Appeal not valid for company " +  appeal.getPenaltyIdentifier().getCompanyNumber() + " : "  + validationError);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }

        String relationshipError = relationshipValidator.validateRelationship(appeal);
        if (relationshipError != null) {
            LOGGER.infoContext("Appeal not valid for company " +  appeal.getPenaltyIdentifier().getCompanyNumber(),
                relationshipError, appealService.createAppealDebugMap(userId, appeal));

            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(relationshipError);
        }

        if (appeal.getReason().getIllness() != null) {
            String illnessDateError = endDateValidator.validateEndDate(appeal);
            if (illnessDateError != null) {
                LOGGER.infoContext(" Appeal not valid for company " + appeal.getPenaltyIdentifier().getCompanyNumber(),
                    illnessDateError, appealService.createAppealDebugMap(userId, appeal));

                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(illnessDateError);
            }
        }

        try {
            final String id = appealService.saveAppeal(appeal, userId);
            LOGGER.infoContext(id, "Create an Appeal for an Late Filing Penalty", appealService.createAppealDebugMap(userId, appeal));

            final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

            return ResponseEntity.created(location).body(id);
        } catch (Exception ex) {
            final Map<String, Object> debugMap = appealService.createAppealDebugMap(userId, appeal);
            LOGGER.error("Unable to create appeal", ex, debugMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{company-number}/appeals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Appeal> getAppealById(@PathVariable("company-number") final String companyId,
                                                @PathVariable("id") final String id) {

        LOGGER.infoContext("Getting Appeal by ID", companyId, appealService.createDebugMapWithoutAppeal(id));
        final Optional<Appeal> appeal = appealService.getAppeal(id);

        return appeal.map(a -> ResponseEntity.status(HttpStatus.OK).body(a))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping(value = "/{company-number}/appeals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appeal>> getAppealsByPenaltyReference(@PathVariable("company-number") final String companyId,
                                                              @RequestParam(value="penaltyReference") final String penaltyReference) {

        LOGGER.info("Getting Appeal by PenaltyReference" +  companyId + " with reference: " + penaltyReference);
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
