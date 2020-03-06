package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
@SpringBootApplication()
public class AppealApplication {

    public static void main (String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }

}
