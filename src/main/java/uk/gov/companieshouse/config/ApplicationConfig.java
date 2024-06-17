package uk.gov.companieshouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.rest.response.ResponseEntityFactory;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Bean
    SerializerFactory serializerFactory() {
        return new SerializerFactory();
    }

    @Bean
    EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }

    @Bean
    ConcurrentHashMap<ServiceResultStatus, ResponseEntityFactory> pluggableResponseEntityFactoryBean(){
        return new ConcurrentHashMap<ServiceResultStatus, ResponseEntityFactory>();
    }
}
