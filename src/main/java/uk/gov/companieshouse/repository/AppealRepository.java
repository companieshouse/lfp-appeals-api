package uk.gov.companieshouse.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.database.entity.AppealEntity;

import java.util.Optional;

@Repository
public interface AppealRepository extends MongoRepository<AppealEntity, String> {

    AppealEntity insert(AppealEntity appeal);
    Optional<AppealEntity> findById(String id);
    void deleteById(String id);
}
