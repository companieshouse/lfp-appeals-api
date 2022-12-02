package uk.gov.companieshouse.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.database.entity.AppealEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<AppealEntity, String> {

    AppealEntity insert(AppealEntity appeal);
    Optional<AppealEntity> findById(String id);

    @Query("{ 'penaltyIdentifier.penaltyReference' : ?0 }")
    List<AppealEntity> findByPenaltyReference(String penaltyReference);

    @Query("{  'penaltyIdentifier.penaltyReference' : ?0 }, { 'penaltyIdentifier.companyNumber': ?1 }")
    List<AppealEntity> findByPenaltyReferenceAndCompanyNumber(String penaltyReference, String companyNumber);

    void deleteById(String id);
}
