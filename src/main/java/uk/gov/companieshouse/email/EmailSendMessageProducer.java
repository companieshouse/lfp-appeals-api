package uk.gov.companieshouse.email;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class EmailSendMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    private final EmailSendMessageFactory emailSendAvroSerializer;
    private final EmailSendKafkaProducer emailSendKafkaProducer;

    public EmailSendMessageProducer(final EmailSendMessageFactory avroSerializer,
                                    final EmailSendKafkaProducer kafkaMessageProducer) {
        this.emailSendAvroSerializer = avroSerializer;
        this.emailSendKafkaProducer = kafkaMessageProducer;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     * @param email EmailSend object encapsulating the message content
     * @throws SerializationException should there be a failure to serialize the EmailSend object
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    public void sendMessage(final EmailSend email, String penaltyReference) {
//        Map<String, Object> logMap = new HashMap<>();
//        LoggingUtils.logWithOrderReference("Sending message to kafka producer", orderReference);
    	LOGGER.info("Sending message to kafka producer: " + penaltyReference);
    	try {
    		final Message message = emailSendAvroSerializer.createMessage(email, penaltyReference);
//        LoggingUtils.logIfNotNull(logMap, LoggingUtils.TOPIC, message.getTopic());
			emailSendKafkaProducer.sendMessage(message, penaltyReference,
			        recordMetadata ->
			            logOffsetFollowingSendIngOfMessage(penaltyReference, recordMetadata));
		} catch (Exception e) {
            final String errorMessage
            	= String.format("Kafka 'email-send' message could not be sent for appeal with penalty reference - %s", penaltyReference);
//		    logMap.put(LoggingUtils.EXCEPTION, e);
//		    LOGGER.error(errorMessage, logMap);
		    throw new ServiceException(errorMessage, e);
		}
    }

    /**
     * Logs the order reference, topic, partition and offset for the item message produced to a Kafka topic.
     * @param orderReference the order reference
     * @param recordMetadata the metadata for a record that has been acknowledged by the server for the message produced
     */
    void logOffsetFollowingSendIngOfMessage(final String orderReference,
                                            final RecordMetadata recordMetadata) {
        //final Map<String, Object> logMapCallback =  createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        //logIfNotNull(logMapCallback, ORDER_REFERENCE_NUMBER, orderReference);
        //LOGGER.info("Message sent to Kafka topic", logMapCallback);
    }
}
