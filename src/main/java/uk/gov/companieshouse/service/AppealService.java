package uk.gov.companieshouse.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.mapper.AppealMapper;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.repository.AppealRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AppealService {

    private final AppealMapper appealMapper;
    private final AppealRepository appealRepository;

    public AppealService(AppealMapper appealMapper, AppealRepository appealRepository) {
        this.appealMapper = appealMapper;
        this.appealRepository = appealRepository;
    }

    public String saveAppeal(Appeal appeal, String userId) {
        appeal.setCreatedBy(new CreatedBy(userId));
        appeal.setCreatedAt(LocalDateTime.now());

        return Optional.ofNullable(appealRepository.insert(this.appealMapper.map(appeal))).map(AppealEntity::getId).orElseThrow(() ->
            new RuntimeException(String.format("Appeal not saved in database for companyNumber: %s, penaltyReference: %s and userId: %s",
                this.appealMapper.map(appeal).getPenaltyIdentifier().getCompanyNumber(), this.appealMapper.map(appeal).getPenaltyIdentifier().getPenaltyReference(), userId)));
    }

    public Optional<Appeal> getAppeal(String id) {
        return appealRepository.findById(id).map(this.appealMapper::map);
    }
}
