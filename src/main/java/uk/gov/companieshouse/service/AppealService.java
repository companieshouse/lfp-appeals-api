package uk.gov.companieshouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.repository.AppealRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppealService {

    private final AppealRepository appealRepository;

    public String createAppeal(String userId, Appeal appeal) {

        appeal.setUserId(userId);

        return appealRepository.insert(appeal).get_id();
    }

}
