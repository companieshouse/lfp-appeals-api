package uk.gov.companieshouse.logging;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@ExtendWith(MockitoExtension.class)
class OpenTelemetryAppenderInitializerTest {

    @Mock
    private OpenTelemetry openTelemetry;

    @Test
    void afterPropertiesSetInstallsTheOpenTelemetryAppender() {
        OpenTelemetryAppenderInitializer initializer = new OpenTelemetryAppenderInitializer(openTelemetry);

        try (MockedStatic<OpenTelemetryAppender> mockedAppender = Mockito.mockStatic(OpenTelemetryAppender.class)) {
            initializer.afterPropertiesSet();

            mockedAppender.verify(() -> OpenTelemetryAppender.install(openTelemetry));
        }
    }

    @Test
    void beanIsAbsentWhenPropertyIsUnset() {
        new ApplicationContextRunner()
            .withUserConfiguration(OpenTelemetryAppenderInitializer.class)
            .withBean(OpenTelemetry.class, () -> openTelemetry)
            .run(context -> assertThat(context).doesNotHaveBean(OpenTelemetryAppenderInitializer.class));
    }

    @Test
    void beanIsPresentWhenPropertyIsEnabled() {
        new ApplicationContextRunner()
            .withUserConfiguration(OpenTelemetryAppenderInitializer.class)
            .withBean(OpenTelemetry.class, () -> openTelemetry)
            .withPropertyValues("management.opentelemetry.enabled=true")
            .run(context -> assertThat(context).hasSingleBean(OpenTelemetryAppenderInitializer.class));
    }
}
