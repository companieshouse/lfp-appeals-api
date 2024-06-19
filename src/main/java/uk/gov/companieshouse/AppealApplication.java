package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class AppealApplication {

    public static final String APPLICATION_NAME_SPACE = "lfp-appeals";
    public static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);

    public static void main (String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }

}
