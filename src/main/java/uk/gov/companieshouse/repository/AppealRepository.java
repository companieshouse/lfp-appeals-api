package uk.gov.companieshouse.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.model.Appeal;

import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<Appeal, String> {

    Appeal insert(Appeal appeal);
    Optional<Appeal> findById(String id);

    @Query("{ 'penaltyIdentifier' : {'companyNumber' : ?0, 'penaltyReference' : ?1 } }")
    Optional<Appeal> findByPenaltyReference(String companyNumber, String penaltyReference);

    void deleteById(String id);
}
