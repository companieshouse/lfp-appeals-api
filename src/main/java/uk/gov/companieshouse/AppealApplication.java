package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import uk.gov.companieshouse.config.AppealsConfig;

@SpringBootApplication
@EnableConfigurationProperties(AppealsConfig.class)
public class AppealApplication {

    public static void main (String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }
}
