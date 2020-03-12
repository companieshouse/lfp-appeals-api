package uk.gov.companieshouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.repository.AppealRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppealService {

    private final AppealRepository appealRepository;

    public String createAppeal(String companyId, Appeal appeal, CreatedBy createdBy) throws Exception {

        appeal.setCreatedBy(createdBy);
        appeal.setCreatedAt(LocalDateTime.now());

        Appeal savedAppeal = appealRepository.insert(appeal);

        return Optional.ofNullable(savedAppeal)
            .map(Appeal::get_id)
            .orElseThrow(() ->
                new Exception(String.format("Appeal not saved in database for company id %s", companyId)));
    }

    public Appeal getAppeal(String id) throws Exception {

        return appealRepository.findById(id)
            .orElseThrow(() -> new Exception(String.format("Appeal not found for id %s", id)));
    }

}
