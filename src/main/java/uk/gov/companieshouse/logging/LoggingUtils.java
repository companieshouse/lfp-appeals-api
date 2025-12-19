package uk.gov.companieshouse.logging;

import uk.gov.companieshouse.AppealApplication;

import java.util.HashMap;
import java.util.Map;

public class LoggingUtils {

    private LoggingUtils() {
        throw new IllegalStateException("A utility class is not to be instantiated");
    }
    public static final String PENALTY_REFERENCE = "penalty_reference_number";
    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE);

    public static Map<String, Object> createLogMap() {
        return new HashMap<>();
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

    public static Logger getLogger() {
        return LOGGER;
    }
}
