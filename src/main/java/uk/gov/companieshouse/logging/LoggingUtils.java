package uk.gov.companieshouse.logging;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.RecordMetadata;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.kafka.message.Message;

public class LoggingUtils {

    private LoggingUtils() {
        throw new IllegalStateException("A utility class is not to be instantiated");
    }

    public static final String TOPIC = "topic";
    public static final String OFFSET = "offset";
    public static final String KEY = "key";
    public static final String PARTITION = "partition";
    public static final String RETRY_ATTEMPT = "retry_attempt";
    public static final String MESSAGE = "message";
    public static final String CURRENT_TOPIC = "current_topic";
    public static final String NEXT_TOPIC = "next_topic";
    public static final String ORDER_RECEIVED_GROUP_ERROR = "order_received_error";
    public static final String PENALTY_REFERENCE = "penalty_reference_number";
    public static final String ORDER_URI = "order_uri";
    public static final String DESCRIPTION_LOG_KEY = "description_key";
    public static final String ITEM_ID = "item_id";
    public static final String EXCEPTION = "exception";
    public static final String PAYMENT_REFERENCE = "payment_reference";
    public static final String COMPANY_NUMBER = "company_number";

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    public static Map<String, Object> createLogMap() {
        return new HashMap<>();
    }

    public static Map<String, Object> createLogMapWithKafkaMessage(Message message) {
        Map<String, Object> logMap = createLogMap();
        logIfNotNull(logMap, TOPIC, message.getTopic());
        logIfNotNull(logMap, PARTITION, message.getPartition());
        logIfNotNull(logMap, OFFSET, message.getOffset());
        return logMap;
    }

    /**
     * Creates a log map containing the required details to track the production of a message to a Kafka topic.
     * @param acknowledgedMessage the {@link RecordMetadata} the metadata for a record that has been acknowledged by
     *                            the server when a message has been produced to a Kafka topic.
     * @return the log map populated with Kafka message production details
     */
	public static Map<String, Object> createLogMapWithAcknowledgedKafkaMessage(
			final RecordMetadata acknowledgedMessage) {
		final Map<String, Object> logMap = createLogMap();
		logIfNotNull(logMap, TOPIC, acknowledgedMessage.topic());
		logIfNotNull(logMap, PARTITION, acknowledgedMessage.partition());
		logIfNotNull(logMap, OFFSET, acknowledgedMessage.offset());
		return logMap;
	}

    public static Map<String, Object> createLogMapWithPenaltyReference(String penaltyReference) {
        Map<String, Object> logMap = createLogMap();
        logIfNotNull(logMap, PENALTY_REFERENCE, penaltyReference);
        return logMap;
    }

    public static void logIfNotNull(Map<String, Object> logMap, String key, Object loggingObject) {
        if (loggingObject != null) {
            logMap.put(key, loggingObject);
        }
    }

    public static Map<String, Object> logWithPenaltyReference(String logMessage,
            String penaltyReference) {
        Map<String, Object> logMap = createLogMapWithPenaltyReference(penaltyReference);
        LOGGER.info(logMessage, logMap);
        return logMap;
    }

    public static Map<String, Object> logMessageWithPenaltyReference(Message message,
            String logMessage, String penaltyReference) {
        Map<String, Object> logMap = createLogMapWithKafkaMessage(message);
        logIfNotNull(logMap, PENALTY_REFERENCE, penaltyReference);
        LOGGER.info(logMessage, logMap);
        return logMap;
    }
    
    public static void logException(String errorMessage, Map<String, Object> logMap) {
	    LOGGER.error(errorMessage, logMap);
    }

    /**
     * Logs the penalty reference, topic, partition and offset for the item message produced to a Kafka topic.
     * @param penaltyReference the penalty reference
     * @param recordMetadata the metadata for a record that has been acknowledged by the server for the message produced
     */
    public static void logOffsetFollowingSendIngOfMessage(final String penaltyReference,
                                            final RecordMetadata recordMetadata) {
        final Map<String, Object> logMapCallback =  LoggingUtils.createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        logIfNotNull(logMapCallback, PENALTY_REFERENCE, penaltyReference);
        LOGGER.info("Message sent to Kafka topic", logMapCallback);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
