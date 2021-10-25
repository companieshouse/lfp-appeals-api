package uk.gov.companieshouse.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.kafka.message.Message;

@ExtendWith(MockitoExtension.class)
class LoggingUtilsTest {
    
    private static final String TOPIC_VALUE = "topic";
    private static final String KEY_VALUE = "key";
    private static final int PARTITION_VALUE = 1;
    private static final long OFFSET_VALUE = 2L;
    private static final String PENALTY_REFERENCE = "penalty reference";
    private static final String LOG_MESSAGE = "log message";

    @Mock
    private RecordMetadata acknowledgedMessage;

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
    @DisplayName("logWithOrderReference returns a populated map")
    void logWithOrderReferenceReturnsPopulatedMap() {
        Map<String, Object> logMap = LoggingUtils.logWithPenaltyReference(LOG_MESSAGE, PENALTY_REFERENCE);
        assertNotNull(logMap);
        assertEquals(2, logMap.size());
        assertEquals(PENALTY_REFERENCE, logMap.get(LoggingUtils.PENALTY_REFERENCE));
        assertEquals(LOG_MESSAGE, logMap.get(LoggingUtils.MESSAGE));
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
    
    @Test
    @DisplayName("createLogMapWithKafkaMessage returns a populated map")
    void createLogMapWithKafkaMessageAllInfo() {
        Message message = createMessageWithTopicAndOffset();
        message.setPartition(PARTITION_VALUE);
        Map<String, Object> logMap = LoggingUtils.createLogMapWithKafkaMessage(message);
        assertNotNull(logMap);
        assertEquals(3, logMap.size());
        assertEquals(TOPIC_VALUE, logMap.get(LoggingUtils.TOPIC));
        assertEquals(PARTITION_VALUE, logMap.get(LoggingUtils.PARTITION));
        assertEquals(OFFSET_VALUE, logMap.get(LoggingUtils.OFFSET));
    }
    
    @Test
    @DisplayName("createLogMapWithKafkaMessage returns a map populated with available info")
    void createLogMapWithKafkaMessageTopicAndOffset() {
        Message message = createMessageWithTopicAndOffset();
        Map<String, Object> logMap = LoggingUtils.createLogMapWithKafkaMessage(message);
        assertNotNull(logMap);
        assertEquals(2, logMap.size());
        assertEquals(TOPIC_VALUE, logMap.get(LoggingUtils.TOPIC));
        assertEquals(OFFSET_VALUE, logMap.get(LoggingUtils.OFFSET));
    }

    @Test
    @DisplayName("createLogMapWithAcknowledgedKafkaMessage returns a populated map")
    void createLogMapWithAcknowledgedKafkaMessageAllInfo() {

        // Given
        when(acknowledgedMessage.topic()).thenReturn(TOPIC_VALUE);
        when(acknowledgedMessage.partition()).thenReturn(PARTITION_VALUE);
        when(acknowledgedMessage.offset()).thenReturn(OFFSET_VALUE);

        // When
        final Map<String, Object> logMap = LoggingUtils.createLogMapWithAcknowledgedKafkaMessage(acknowledgedMessage);

        // Then
        assertNotNull(logMap);
        assertEquals(3, logMap.size());
        assertEquals(TOPIC_VALUE, logMap.get(LoggingUtils.TOPIC));
        assertEquals(PARTITION_VALUE, logMap.get(LoggingUtils.PARTITION));
        assertEquals(OFFSET_VALUE, logMap.get(LoggingUtils.OFFSET));
    }
    
    @Test
    @DisplayName("logMessageWithOrderReference returns a populated map")
    void logMessageWithOrderReferenceReturnsPopulatedMap() {
        Message message = createMessageWithTopicAndOffset();
        Map<String, Object> logMap = LoggingUtils.logMessageWithPenaltyReference(message, LOG_MESSAGE, PENALTY_REFERENCE);
        assertNotNull(logMap);
        assertEquals(4, logMap.size());
        assertEquals(TOPIC_VALUE, logMap.get(LoggingUtils.TOPIC));
        assertEquals(OFFSET_VALUE, logMap.get(LoggingUtils.OFFSET));
        assertEquals(PENALTY_REFERENCE, logMap.get(LoggingUtils.PENALTY_REFERENCE));
        assertEquals(LOG_MESSAGE, logMap.get(LoggingUtils.MESSAGE));
    }

    private Message createMessageWithTopicAndOffset() {
        Message message = new Message();
        message.setTopic(TOPIC_VALUE);
        message.setOffset(OFFSET_VALUE);
        return message;
    }
}
