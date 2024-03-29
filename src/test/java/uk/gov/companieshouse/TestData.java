package uk.gov.companieshouse;

import java.time.LocalDateTime;

public class TestData {

    public static final String ID = "APPEAL#99";
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2010, 12, 31, 23, 59);
    public static final String USER_ID = "USER#1";
    public static final String YOUR_NAME = "User Userson";
    public static final String RELATIONSHIP = "relationship";
    public static final String EMAIL = "user@example.com";
    public static final String COMPANY_NUMBER = "12345678";
    public static final String COMPANY_NAME = "Test company";
    public static final String PENALTY_REFERENCE = "A1234567";
    public static final String TITLE = "Some title";
    public static final String DESCRIPTION = "Some description";
    public static final String ILL_PERSON = "someoneElse";
    public static final String OTHER_PERSON = "other person";
    public static final String ILLNESS_START = "2021-01-01";
    public static final boolean CONTINUED_ILLNESS = false;
    public static final String ILLNESS_END = "2021-02-01";
    public static final String ILLNESS_IMPACT_FURTHER_INFORMATION = "further information";
    public static final String ATTACHMENT_ID = "FILE#1";
    public static final String ATTACHMENT_NAME = "file.txt";
    public static final String CONTENT_TYPE = "plain/text";
    public static final int ATTACHMENT_SIZE = 100;
    public static final String ATTACHMENT_URL = "http://localhost/appeal-a-penalty/download/prompt/1?c=00345567";
    public static final String RELATIONSHIP_ERROR_MESSAGE = "Createdby.RelationshipToCompany must not be null when supplying Other Reason";
    public static final String EXCEPTION_MESSAGE = "BAD THINGS";
}
