package uk.gov.companieshouse.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.logging.LoggingUtils.PENALTY_REFERENCE;

import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.exception.ServiceException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;

/**
 * Unit tests the {@link EmailSendMessageProducer} class.
 */
@ExtendWith(MockitoExtension.class)
class EmailSendMessageProducerTest {

    @InjectMocks
    private EmailSendMessageProducer messageProducerUnderTest;

    @Mock
    private EmailSendMessageFactory emailSendMessageFactory;

    @Mock
    private EmailSendKafkaProducer emailSendKafkaProducer;

    @Mock
    private Message message;

    @Mock
    private Logger logger;

    @Mock
    private EmailSend emailSend;

    @Test
    @DisplayName("sendMessage delegates message creation to EmailSendMessageFactory")
    void sendMessageDelegatesMessageCreation() throws Exception {

        // Given
        when(emailSendMessageFactory.createMessage(emailSend, PENALTY_REFERENCE)).thenReturn(message);

        // When
        messageProducerUnderTest.sendMessage(emailSend, PENALTY_REFERENCE);

        // Then
        verify(emailSendMessageFactory).createMessage(emailSend, PENALTY_REFERENCE);

    }

    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("sendMessage delegates message sending to EmailSendKafkaProducer")
    void sendMessageDelegatesMessageSending() throws Exception {

        // Given
        when(emailSendMessageFactory.createMessage(emailSend, PENALTY_REFERENCE)).thenReturn(message);

        // When
        messageProducerUnderTest.sendMessage(emailSend, PENALTY_REFERENCE);

        // Then
        verify(emailSendKafkaProducer).sendMessage(eq(message), eq(PENALTY_REFERENCE), any(Consumer.class));

    }

    @Test
    @DisplayName("Call to createMessage throws SerializationException")
    void createMessageThrowsSerializationException() throws Exception {

    	String errorMessage = "Kafka 'email-send' message could not be sent for appeal with penalty reference - " + PENALTY_REFERENCE;
    	
        final SerializationException exception = new SerializationException(errorMessage);

        // Given
        when(emailSendMessageFactory.createMessage(emailSend, PENALTY_REFERENCE)).thenThrow(exception);

        // When
        ServiceException thrown = assertThrows(ServiceException.class, () -> messageProducerUnderTest.sendMessage(emailSend, PENALTY_REFERENCE));

        // Then
        assertEquals(exception.getMessage(), thrown.getMessage());
        assertEquals(exception, thrown.getCause());
    }
}
