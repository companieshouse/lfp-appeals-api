package uk.gov.companieshouse.email;

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
     * @param message certificate or certified copy appeal message to be produced to the <code>email-send</code> topic
     * @param orderReference the reference of the order
     * @param asyncResponseLogger RecordMetadata {@link Consumer} that can be implemented to allow the logging of
     *                            the offset once the message has been produced
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    public void sendMessage(final Message message,
                            final String orderReference,
                            final Consumer<RecordMetadata> asyncResponseLogger)
            throws ExecutionException, InterruptedException {
        LOGGER.info("Sending message to Kafka");

        final Future<RecordMetadata> recordMetadataFuture = getChKafkaProducer().sendAndReturnFuture(message);
        asyncResponseLogger.accept(recordMetadataFuture.get());
    }

}
