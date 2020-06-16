package uk.gov.companieshouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChipsConfiguration {

    @Value("${chips.enabled}")
    private boolean chipsEnabled;

    @Value("${chips.restservice.url}")
    private String chipsRestServiceUrl;

    public boolean isChipsEnabled() {
        return chipsEnabled;
    }

    public String getChipsRestServiceUrl() {
        return chipsRestServiceUrl;
    }
}
