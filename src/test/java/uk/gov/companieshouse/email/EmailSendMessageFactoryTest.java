package uk.gov.companieshouse.email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@SpringBootTest
@DirtiesContext
class EmailSendMessageFactoryTest {
	
	@Autowired
    private SerializerFactory serializerFactory;

    private static final String APP_ID = "App Id";
    private static final String EMAIL_DATA = "Message content";
    private static final String EMAIL_ADDR = "someone@example.com";
    private static final String MSG_ID = "Message Id";
    private static final String MSG_TYPE = "Message type";
    private static final String PENALTY_REF = "A12345678";
    private static final String CREATED_AT = "2020-08-25T09:27:09.519+01:00";

    private static final String TOPIC = "email-send";

    @Test
    void createMessageSuccessfullyCreatesMessage() throws Exception {
        // Given
        EmailSendMessageFactory messageFactory = new EmailSendMessageFactory(serializerFactory);

        // When
        Message message = messageFactory.createMessage(createEmailSend(), PENALTY_REF);
        String actualContent = new String(message.getValue());
        
        // Then
        AvroSerializer<EmailSend> serializer = serializerFactory.getGenericRecordSerializer(EmailSend.class);
        String expectedContent = new String(serializer.toBinary(createEmailSend()));
        Assertions.assertEquals(expectedContent, actualContent);
        Assertions.assertEquals(TOPIC, message.getTopic());
    }

    private EmailSend createEmailSend() {
        EmailSend emailSend = new EmailSend();
        emailSend.setAppId(APP_ID);
        emailSend.setData(EMAIL_DATA);
        emailSend.setEmailAddress(EMAIL_ADDR);
        emailSend.setMessageId(MSG_ID);
        emailSend.setMessageType(MSG_TYPE);
        emailSend.setCreatedAt(CREATED_AT);

        return emailSend;
    }
}
