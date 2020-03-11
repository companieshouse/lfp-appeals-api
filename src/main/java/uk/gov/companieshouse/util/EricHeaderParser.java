package uk.gov.companieshouse.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.CreatedBy;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class EricHeaderParser {

    private static final String DELIMITER = ";";
    private static final String EMAIL_IDENTIFIER = "@";
    private static final String ERIC_FORENAME_KEY = "forename=";
    private static final String ERIC_FORENAME_KEY_UTF8 = "forename*=";
    private static final String ERIC_SURNAME_KEY = "surname=";
    private static final String ERIC_SURNAME_KEY_UTF8 = "surname*=";


    public CreatedBy retrieveUser(String userId, String authorisedUser) {

        CreatedBy createdBy = new CreatedBy();
        createdBy.setId(userId);
        createdBy.setForename(getForename(authorisedUser));
        createdBy.setSurname(getSurname(authorisedUser));
        createdBy.setEmail(getEmail(authorisedUser));

        return createdBy;
    }

    private String getEmail(String authorisedUser) {

        String email = null;

        String[] values = authorisedUser.split(DELIMITER);

        if (values.length > 0) {

            String firstValue = values[0];
            if (firstValue.contains(EMAIL_IDENTIFIER)) {
                email = firstValue;
            }

        }

        return email;
    }

    private String getForename(String authorisedUser) {

        String forename = getNameFromAuthorisedUser(authorisedUser, ERIC_FORENAME_KEY, DELIMITER);
        if (forename == null) {
            forename = getNameFromAuthorisedUser(authorisedUser, ERIC_FORENAME_KEY_UTF8, DELIMITER);
            if (forename != null) {
                forename = decodeUTF8(forename);
            }
        }
        return forename;
    }

    private String getSurname(String authorisedUser) {

     String surname = getNameFromAuthorisedUser(authorisedUser, ERIC_SURNAME_KEY, null);
        if (surname == null) {
            surname = getNameFromAuthorisedUser(authorisedUser, ERIC_SURNAME_KEY_UTF8, null);
            if (surname != null) {
                surname = decodeUTF8(surname);
            }
        }

     return surname;
    }

    private String getNameFromAuthorisedUser(String authorisedUser, String key, String delimiter) {

        String name = null;

        int nameStartIndex = authorisedUser.indexOf(key);

        if (nameStartIndex >= 0) {
            nameStartIndex += key.length();
            if (delimiter == null) {
                name = authorisedUser.substring(nameStartIndex);
            } else {
                int nameEndIndex = authorisedUser.indexOf(delimiter, nameStartIndex);
                if (nameEndIndex >= nameStartIndex) {
                    name = authorisedUser.substring(nameStartIndex, nameEndIndex);
                }
            }
        }

        return name;
    }

    private static String decodeUTF8(String utf8String) {
        String utf8Prefix = "UTF-8''";
        utf8String = StringUtils.remove(utf8String, utf8Prefix);
        return URLDecoder.decode(utf8String, StandardCharsets.UTF_8);
    }
}
