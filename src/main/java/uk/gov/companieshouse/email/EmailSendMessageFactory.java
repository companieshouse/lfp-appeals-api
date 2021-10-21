package uk.gov.companieshouse.email;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.LoggingUtils;

@Service
public class EmailSendMessageFactory {

	private final SerializerFactory serializerFactory;
	private static final String EMAIL_SEND_TOPIC = "email-send";

    public static final Logger LOGGER = LoggerFactory.getLogger(AppealApplication.APP_NAMESPACE);

    public EmailSendMessageFactory(SerializerFactory serializer) {
		serializerFactory = serializer;
	}

	/**
	 * Creates an email-send avro message.
	 * 
	 * @param emailSend email-send object
	 * @return email-send avro message
	 * @throws SerializationException should there be a failure to serialize the EmailSend object
	 */
	public Message createMessage(final EmailSend emailSend, String penaltyReference) throws SerializationException {
        Map<String, Object> logMap = LoggingUtils.createLogMapWithPenaltyReference(penaltyReference);
	    logMap.put(LoggingUtils.TOPIC, EMAIL_SEND_TOPIC);
        LOGGER.infoContext(penaltyReference, "Create kafka message for penalty reference", logMap);
		final AvroSerializer<EmailSend> serializer =
				serializerFactory.getGenericRecordSerializer(EmailSend.class);
		final Message message = new Message();
		message.setValue(serializer.toBinary(emailSend));
		message.setTopic(EMAIL_SEND_TOPIC);
		message.setTimestamp(new Date().getTime());
		LOGGER.info("Kafka message created");
		return message;
	}
}
