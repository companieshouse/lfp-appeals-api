package uk.gov.companieshouse.email;

import email.email_send;
import org.springframework.stereotype.Component;

/**
 * Extends {@link email_send} to make it available as an apparently Java naming and packaging conventions compliant
 * class without requiring changes to the Avro schema from which <code>email_send</code> is generated.
 */
@Component
public class EmailSend extends email_send{
}
