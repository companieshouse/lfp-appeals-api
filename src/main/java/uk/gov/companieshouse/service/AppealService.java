package uk.gov.companieshouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.repository.AppealRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppealService {

    private final AppealRepository appealRepository;

    public String createAppeal(String userId, String companyId, Appeal appeal) throws ServiceException {

        appeal.setUserId(userId);
        appeal.setCreatedAt(LocalDateTime.now());

        Appeal savedAppeal = appealRepository.insert(appeal);

        return Optional.ofNullable(savedAppeal)
            .map(Appeal::get_id)
            .orElseThrow(() ->
                new ServiceException(String.format("Appeal not saved in database for user id %s and company id %s",
                    userId, companyId)));
    }

}
