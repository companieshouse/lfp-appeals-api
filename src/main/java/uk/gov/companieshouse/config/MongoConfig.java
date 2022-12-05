package uk.gov.companieshouse.config;

import com.mongodb.ReadConcern;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

public class MongoConfig {

    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        var transactionOptions = TransactionOptions
            .builder()
            .readConcern(ReadConcern.LOCAL)
            .writeConcern(WriteConcern.W1)
            .build();
        return new MongoTransactionManager(dbFactory, transactionOptions);
    }
}
