package uk.gov.companieshouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyNumberConfiguration {
    @Value("${companyNumber.prefixes}")
    private String companyNumberPrefixes;

    public String getPrefixes() {
        return companyNumberPrefixes;
    }
}