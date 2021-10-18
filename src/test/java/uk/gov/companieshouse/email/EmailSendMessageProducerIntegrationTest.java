package uk.gov.companieshouse.email;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka
class EmailSendMessageProducerIntegrationTest {

    private static final String ORDER_REFERENCE = "ORD-432118-793830";

    private static final DateTimeFormatter TIME_OF_PAYMENT_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' hh:mm");

//    @Autowired
//    EmailSendMessageProducer emailSendMessageProducerUnderTest;

//    @Autowired
//    TestEmailSendMessageConsumer testEmailSendMessageConsumer;

    @Autowired
    SerializerFactory serializerFactory;

    @Autowired
    ObjectMapper objectMapper;

//    @Test
//    @DisplayName("Can send certificate order confirmation to email-send")
//    void testSendCertificateOrderConfirmationMessageToKafkaTopic() throws Exception {
//
//        // Given an EmailSend object is created
//        final CertificateOrderConfirmation confirmation = new CertificateOrderConfirmation();
//        confirmation.setTo("nobody@nowhere.com");
//
//        confirmation.setForename("Jenny");
//        confirmation.setSurname("Wilson");
//
//        confirmation.setAddressLine1("Kemp House Capital Office");
//        confirmation.setAddressLine2("LTD");
//        confirmation.setHouseName("Kemp House");
//        confirmation.setHouseNumberStreetName("152-160 City Road");
//        confirmation.setCity("London");
//        confirmation.setPostCode("EC1V 2NX");
//        confirmation.setOrderReferenceNumber(ORDER_REFERENCE);
//        confirmation.setEmailAddress("mail@globaloffshore.com");
//        confirmation.setDeliveryMethod("Standard delivery");
//        confirmation.setFeeAmount("15");
//        confirmation.setTimeOfPayment(TIME_OF_PAYMENT_FORMATTER.format(LocalDateTime.now()));
//        confirmation.setPaymentReference("RS5VSNDRE");
//        confirmation.setCompanyName("GLOBAL OFFSHORE HOST LIMITED");
//        confirmation.setCompanyNumber("11260147");
//        confirmation.setCertificateType("Incorporation with all company name changes");
//        confirmation.setCertificateIncludes(new String[]{
//                "Statement of good standing",
//                "Registered office address",
//                "Directors",
//                "Secretaries",
//                "Company objects"
//        });
//
//        final EmailSend email = new EmailSend();
//        email.setAppId("item-handler.certificate-order-confirmation");
//        email.setEmailAddress("test@test.com");
//        email.setMessageId(UUID.randomUUID().toString());
//        email.setMessageType("certificate_order_confirmation_email");
//        email.setData(objectMapper.writeValueAsString(confirmation));
//        email.setCreatedAt(LocalDateTime.now().toString());
//
//        // When email-send message is sent to kafka topic
//        final List<Message> messages = sendAndConsumeMessage(email);
//
//        // Then we can successfully consume the message.
//        assertThat(messages.isEmpty(), is(false));
//        byte[] consumedMessageSerialized = messages.get(0).getValue();
//        final String deserializedConsumedMessage = new String(consumedMessageSerialized);
//
//        // and it matches the serialized email-send object sent
//        final AvroSerializer<EmailSend> serializer = serializerFactory.getGenericRecordSerializer(EmailSend.class);
//        final byte[] serializedEmail = serializer.toBinary(email);
//        final String deserializedEmail = new String(serializedEmail);
//
//        assertEquals(deserializedConsumedMessage, deserializedEmail);
//    }
//
//    @Test
//    @DisplayName("Can send missing image delivery order confirmation to email-send")
//    void testSendMissingImageDeliveryOrderConfirmationMessageToKafkaTopic() throws Exception {
//
//        // Given an EmailSend object is created
//        final ItemOrderConfirmation confirmation = new ItemOrderConfirmation();
//        confirmation.setTo("nobody@nowhere.com");
//
//        confirmation.setForename("Jenny");
//        confirmation.setSurname("Wilson");
//
//        confirmation.setAddressLine1("Kemp House Capital Office");
//        confirmation.setAddressLine2("LTD");
//        confirmation.setHouseName("Kemp House");
//        confirmation.setHouseNumberStreetName("152-160 City Road");
//        confirmation.setCity("London");
//        confirmation.setPostCode("EC1V 2NX");
//        confirmation.setOrderReferenceNumber(ORDER_REFERENCE);
//        confirmation.setEmailAddress("mail@globaloffshore.com");
//        confirmation.setTimeOfPayment(TIME_OF_PAYMENT_FORMATTER.format(LocalDateTime.now()));
//        confirmation.setPaymentReference("RS5VSNDRE");
//        confirmation.setCompanyName("GLOBAL OFFSHORE HOST LIMITED");
//        confirmation.setCompanyNumber("11260147");
//        final ItemDetails missingImage = new ItemDetails();
//        missingImage.setDateFiled("15 Feb 2018");
//        missingImage.setDescription("Appointment of Ms Sharon Michelle White as a director on 4 February 2020");
//        missingImage.setFee("3");
//        missingImage.setType("AP01");
//        confirmation.setItemDetails(singletonList(missingImage));
//
//        final EmailSend email = new EmailSend();
//        email.setAppId("item-handler.missing-image-delivery-order-confirmation");
//        email.setEmailAddress("test@test.com");
//        email.setMessageId(UUID.randomUUID().toString());
//        email.setMessageType("missing_image_delivery_order_confirmation_email");
//        email.setData(objectMapper.writeValueAsString(confirmation));
//        email.setCreatedAt(LocalDateTime.now().toString());
//
//        // When email-send message is sent to kafka topic
//        final List<Message> messages = sendAndConsumeMessage(email);
//
//        // Then we can successfully consume the message.
//        assertThat(messages.isEmpty(), is(false));
//        byte[] consumedMessageSerialized = messages.get(0).getValue();
//        final String deserializedConsumedMessage = new String(consumedMessageSerialized);
//
//        // and it matches the serialized email-send object sent
//        final AvroSerializer<EmailSend> serializer = serializerFactory.getGenericRecordSerializer(EmailSend.class);
//        final byte[] serializedEmail = serializer.toBinary(email);
//        final String deserializedEmail = new String(serializedEmail);
//
//        assertEquals(deserializedConsumedMessage, deserializedEmail);
//    }
//
//    private List<Message> sendAndConsumeMessage(final EmailSend email) throws Exception {
//        List<Message> messages;
//        testEmailSendMessageConsumer.connect();
//        int count = 0;
//        do {
//            messages = testEmailSendMessageConsumer.pollConsumerGroup();
//            sendMessageOnce(email, count);
//            count++;
//        } while (messages.isEmpty() && count < 15);
//
//        return messages;
//    }

//    private void sendMessageOnce(final EmailSend email, final int count) throws Exception {
//        if (count == 0) {
//            emailSendMessageProducerUnderTest.sendMessage(email, ORDER_REFERENCE);
//        }
//    }

}
