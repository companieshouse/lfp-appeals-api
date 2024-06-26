package uk.gov.companieshouse.email;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.LoggingUtils;

@Service
public class EmailSendMessageProducer {

	private static final String EXCEPTION_MESSAGE = "Kafka 'email-send' message could not be sent for appeal with penalty reference - %s";

    private final EmailSendMessageFactory emailSendAvroSerializer;


    private final EmailSendKafkaProducer emailSendKafkaProducer;

    @Autowired
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
        Map<String, Object> logMap = new HashMap<>();
        LoggingUtils.logWithPenaltyReference("Sending message to kafka producer", penaltyReference);
    	try {
    		Message message;
    		message = emailSendAvroSerializer.createMessage(email, penaltyReference);
			LoggingUtils.logIfNotNull(logMap, LoggingUtils.TOPIC, message.getTopic());
			emailSendKafkaProducer.sendMessage(message, penaltyReference,
				        recordMetadata ->
				            LoggingUtils.logOffsetFollowingSendIngOfMessage(penaltyReference, recordMetadata));
		} catch (SerializationException | ExecutionException e) {
            final var errorMessage
            	= String.format(EXCEPTION_MESSAGE, penaltyReference);
		    logMap.put(LoggingUtils.EXCEPTION, e);
		    LoggingUtils.logException(errorMessage, logMap);
            throw new ServiceException(errorMessage, e);
		} catch (InterruptedException e) {
	        final var errorMessage
	        	= String.format(EXCEPTION_MESSAGE, penaltyReference);
		    logMap.put(LoggingUtils.EXCEPTION, e);
		    LoggingUtils.logException(errorMessage, logMap);
		    Thread.currentThread().interrupt();
	        throw new ServiceException(errorMessage, e);
		}
    }
}
