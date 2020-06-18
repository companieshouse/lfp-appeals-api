package uk.gov.companieshouse.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.database.entity.AppealEntity;

import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<AppealEntity, String> {

    AppealEntity insert(AppealEntity appeal);
    Optional<AppealEntity> findById(String id);

    @Query("{ 'penaltyIdentifier' : { 'companyNumber' : ?0 , 'penaltyReference' : ?1 } }")
    Optional<AppealEntity> findByPenaltyReference(String companyNumber, String penaltyReference);

    void deleteById(String id);
}
