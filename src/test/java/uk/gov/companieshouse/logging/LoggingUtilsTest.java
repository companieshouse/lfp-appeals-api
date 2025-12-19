package uk.gov.companieshouse.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class LoggingUtilsTest {
    
    private static final String KEY_VALUE = "key";
    private static final String PENALTY_REFERENCE = "penalty reference";

    @Test
    @DisplayName("createLogMap returns a new log map")
    void createLogMapReturnsLogMap() {
        Map<String, Object> logMap = LoggingUtils.createLogMap();
        assertNotNull(logMap);
    }

    @Test
    @DisplayName("logIfNotNull populates a log map")
    void logIfNotNullPopulatesLogMap() {
        Map<String, Object> logMap = LoggingUtils.createLogMap();
        String key = KEY_VALUE;
        String testObject = "test";

        LoggingUtils.logIfNotNull(logMap, key, testObject);

        assertNotNull(logMap);
        assertEquals(logMap.get(key), testObject);
    }

    @Test
    @DisplayName("createLogMapWithOrderReference returns a populated map")
    void createLogMapWithOrderReferenceReturnsPopulatedMap() {
        Map<String, Object> logMap = LoggingUtils.createLogMapWithPenaltyReference(PENALTY_REFERENCE);
        assertNotNull(logMap);
        assertEquals(1, logMap.size());
        assertEquals(PENALTY_REFERENCE, logMap.get(LoggingUtils.PENALTY_REFERENCE));
    }
    
    @Test
    @DisplayName("createLogMapWithOrderReference returns an empty map")
    void createLogMapWithOrderReferenceReturnsEmptyMap() {
        Map<String, Object> logMap = LoggingUtils.createLogMapWithPenaltyReference(null);
        assertNotNull(logMap);
        assertEquals(0, logMap.size());
    }
    
    @Test
    @DisplayName("getLogger returns a logger object")
    void getLoggerReturnsLoggerObject() {
        Logger logger = LoggingUtils.getLogger();
        assertNotNull(logger);
    }

}
