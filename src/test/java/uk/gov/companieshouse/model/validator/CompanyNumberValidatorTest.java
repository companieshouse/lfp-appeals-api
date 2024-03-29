package uk.gov.companieshouse.model.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompanyNumberValidatorTest {

    @DisplayName("Throw an exception when input list is in wrong format")
    @Test
    void shouldThrowAnExceptionWhenInputListIsInWrongFormat() {

        List<String> invalidLists = List.of(",", "A,[.", "A.BC.D");

        invalidLists.forEach(prefixList -> assertThrows(IllegalArgumentException.class,
                () -> new CompanyNumberValidator(prefixList), prefixList));

    }

    @DisplayName("Create factory successfully when valid lists are provided")
    @Test
    void shouldCreateFactorySuccessfullyWhenValidListsProvided() {
        List<String> validLists = List.of("NI", "AB,CD,EF", "A,B,C,D,F", "AB,cd,e,F");
        validLists.forEach(prefixList -> assertDoesNotThrow(() -> new CompanyNumberValidator(prefixList), prefixList));
    }

    @DisplayName("Return false when prefix is not allowed")
    @Test
    void shouldReturnFalseWhenPrefixIsNotAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberValidator companyNumberValidator = new CompanyNumberValidator(prefixes);

        List<String> invalidCompanyNumbers = List.of("TY000001", "PTABC0123", "XC999999", "T0000001");

        boolean isAllInvalid = invalidCompanyNumbers.stream()
                .noneMatch(value -> companyNumberValidator.isValid(value, null));

        assertTrue(isAllInvalid);

    }

    @DisplayName("Return true when prefixes are allowed")
    @Test
    void shouldReturnTrueWhenPrefixesAreAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberValidator companyNumberValidator = new CompanyNumberValidator(prefixes);

        List<String> validUpperCaseCompanyNumbers = List.of("NI000000", "SC123123", "OC123123", "SO123123", "R0000000",
                "R123", "123", "AP123456");

        List<String> validLowerCaseCompanyNumbers = validUpperCaseCompanyNumbers.stream().map(String::toLowerCase)
                .collect(Collectors.toList());

        boolean isAllValid = Stream.of(validUpperCaseCompanyNumbers, validLowerCaseCompanyNumbers)
                .flatMap(Collection::stream).allMatch(value -> companyNumberValidator.isValid(value, null));

        assertTrue(isAllValid);

    }

    @DisplayName("Return false when value is null")
    @Test
    void shouldReturnFalseWhenValueIsNull() {
        assertFalse(new CompanyNumberValidator("A,B,C").isValid(null, null));
    }

}
