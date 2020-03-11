package uk.gov.companieshouse.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.companieshouse.model.CreatedBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class EricHeaderParserTest {

    private static final String AUTH_USER = "demo@ch.gov.uk; forename=demoForename; surname=demoSurname";
    private static final String UTF8_AUTH_USER = "demo@ch.gov.uk; forename*=UTF-8''demo%20%3BForename; surname*=UTF-8''demo%3BSurname";
    private static final String ID = "123";

    @InjectMocks
    private EricHeaderParser ericHeaderParser;

    @Test
    public void testRetrieveUser() {

        CreatedBy actual = ericHeaderParser.retrieveUser(ID, AUTH_USER);

        assertEquals(ID, actual.getId());
        assertEquals("demoForename", actual.getForename());
        assertEquals("demoSurname", actual.getSurname());
        assertEquals("demo@ch.gov.uk", actual.getEmail());
    }

    @Test
    public void testRetrieveUser_UTF8() {

        CreatedBy actual = ericHeaderParser.retrieveUser(ID, UTF8_AUTH_USER);

        assertEquals(ID, actual.getId());
        assertEquals("demo ;Forename", actual.getForename());
        assertEquals("demo;Surname", actual.getSurname());
        assertEquals("demo@ch.gov.uk", actual.getEmail());
    }


    @Test
    public void testRetrieveUser_blankAuthorisedUserHeader() {

        CreatedBy actual = ericHeaderParser.retrieveUser("123", "");

        assertEquals(ID, actual.getId());
        assertNull(actual.getForename());
        assertNull(actual.getSurname());
        assertNull(actual.getEmail());
    }
}
