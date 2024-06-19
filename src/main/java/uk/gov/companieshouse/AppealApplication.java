package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class AppealApplication {

    public static final String APP_NAMESPACE = "lfp-appeals";
    public static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    public static void main (String[] args) {
        SpringApplication.run(AppealApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
