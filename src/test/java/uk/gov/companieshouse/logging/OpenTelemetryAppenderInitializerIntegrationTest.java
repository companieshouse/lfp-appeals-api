package uk.gov.companieshouse.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;

/**
 * Loads the real application context with the {@code management.opentelemetry.*} properties
 * populated (mirroring {@code src/main/resources/application.yml}, since the test classpath's
 * own {@code application.yml} shadows the main one and never exercises these properties
 * otherwise) to confirm the context starts successfully with OpenTelemetry enabled.
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = {
    "management.opentelemetry.enabled=true",
    "OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318",
    "management.opentelemetry.logging.export.otlp.endpoint=http://localhost:4318/v1/logs",
    "management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces",
    "management.opentelemetry.metrics.export.otlp.endpoint=http://localhost:4318/v1/metrics"
})
class OpenTelemetryAppenderInitializerIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextStartsWithOpenTelemetryEnabledAndRegistersTheAppenderInitializer() {
        assertThat(applicationContext.getBean(OpenTelemetryAppenderInitializer.class)).isNotNull();
    }
}
