package uk.gov.companieshouse.email;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.logging.LoggingUtils.PENALTY_REFERENCE;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggingUtils;

/**
 * Unit tests the {@link ItemKafkaProducer} class.
 */
@ExtendWith(MockitoExtension.class)
class EmailSendKafkaProducerTest {

    @InjectMocks
    private EmailSendKafkaProducer producerUnderTest;

    @Mock
    private Message message;

    @Mock
    private Consumer<RecordMetadata> consumer;

    @Mock
    private CHKafkaProducer chKafkaProducer;

    @Mock
    private Future<RecordMetadata> recordMetadataFuture;

    @Mock
    private Logger logger;

    @Mock
    private ProducerConfig producerConfig;
    
    @Mock
    private LoggingUtils loggingUtils;

    @Test
    @DisplayName("sendMessage() delegates message sending to ChKafkaProducer")
    void sendMessageDelegatesToChKafkaProducer() throws ExecutionException, InterruptedException {

        // Given
        when(chKafkaProducer.sendAndReturnFuture(message)).thenReturn(recordMetadataFuture);

        // When
        producerUnderTest.sendMessage(message, PENALTY_REFERENCE, consumer);

        // Then
        verify(chKafkaProducer).sendAndReturnFuture(message);

    }
}
