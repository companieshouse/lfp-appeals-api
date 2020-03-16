package uk.gov.companieshouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.exception.AppealNotFoundException;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.repository.AppealRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppealService {

    private final AppealRepository appealRepository;

    public String saveAppeal(String companyId, Appeal appeal, String ericIdentity) throws Exception {

        appeal.setCreatedBy(CreatedBy.builder().id(ericIdentity).build());
        appeal.setCreatedAt(LocalDateTime.now());

        return Optional.ofNullable(appealRepository.insert(appeal)).map(Appeal::getId).orElseThrow(() ->
            new Exception(String.format("Appeal not saved in database for company id %s", companyId)));
    }

    public Appeal getAppeal(String id) {

        return appealRepository.findById(id).orElseThrow(() -> new AppealNotFoundException(id));
    }
}
