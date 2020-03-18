package uk.gov.companieshouse.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.model.Appeal;

import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<Appeal, String> {

    Appeal insert(Appeal appeal);
    Optional<Appeal> findById(String id);
}
