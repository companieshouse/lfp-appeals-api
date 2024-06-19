package uk.gov.companieshouse.email;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class EmailSendKafkaProducer extends KafkaProducer  {

    public static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    /**
     * Sends message to Kafka topic
     * @param message Appeal message to be produced to the <code>email-send</code> topic
     * @param penaltyReference The reference of the penalty
     * @param asyncResponseLogger RecordMetadata {@link Consumer} that can be implemented to allow the logging of
     *                            the offset once the message has been produced
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    public void sendMessage(final Message message,
                            final String penaltyReference,
                            final Consumer<RecordMetadata> asyncResponseLogger)
            throws ExecutionException, InterruptedException {
        Map<String, Object> logMap = new HashMap<>();
    	logMap.put("message", message.toString());
        LOGGER.infoContext(penaltyReference, "Sending message to Kafka", logMap);

        final Future<RecordMetadata> recordMetadataFuture = getChKafkaProducer().sendAndReturnFuture(message);
        asyncResponseLogger.accept(recordMetadataFuture.get());
    }
}
