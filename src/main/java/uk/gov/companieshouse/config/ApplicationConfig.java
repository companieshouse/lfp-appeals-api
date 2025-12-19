package uk.gov.companieshouse.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.rest.response.ResponseEntityFactory;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
    

    @Bean
    EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }

    @Bean
    ConcurrentHashMap<ServiceResultStatus, ResponseEntityFactory> pluggableResponseEntityFactoryBean(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    @Primary
    public InternalApiClient internalApiClient() {
        return ApiSdkManager.getPrivateSDK();
    }

    @Bean
    @Qualifier("emailInternalApiClient")
    InternalApiClient emailInternalApiClient(
        @Value("${chs.api.key}") String apiKey,
        @Value("${chs.kafka.api.url}") String apiUrl) {
        var internalApiClient = new InternalApiClient(new ApiKeyHttpClient(apiKey));
        internalApiClient.setBasePath(apiUrl);
        return internalApiClient;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    
}
