package uk.gov.companieshouse.model.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.companieshouse.model.validator.CompanyNumberValidator;

@ExtendWith(SpringExtension.class)
public class CompanyNumberValidatorTest {

    @Test
    void shouldThrowAnExceptionWhenInputListIsInWrongFormat() {

        List<String> invalidLists = List.of(",", "A,[.", "A.BC.D");

        invalidLists.forEach(prefixList -> {
            assertThrows(IllegalArgumentException.class, () -> new CompanyNumberValidator(prefixList), prefixList);
        });

    }

    @Test
    void shouldCreateFactorySuccessfulyWhenValidListsProvided() {
        List<String> validLists = List.of("NI", "AB,CD,EF", "A,B,C,D,F", "AB,cd,e,F");
        validLists.forEach(prefixList -> {
            assertDoesNotThrow(() -> new CompanyNumberValidator(prefixList), prefixList);
        });
    }

    @Test
    void shouldReturnFalseWhenPrefixIsNotAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberValidator companyNumberValidator = new CompanyNumberValidator(prefixes);

        List<String> invalidCompanyNumbers = List.of("TY000001", "PTABC0123", "XC999999", "T0000001");

        boolean isAllInvalid = invalidCompanyNumbers.stream().map(value -> companyNumberValidator.isValid(value, null))
                .allMatch(result -> result == false);

        assertTrue(isAllInvalid);

    }

    @Test
    void shouldReturnTrueWhenPrefixesAreAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberValidator companyNumberValidator = new CompanyNumberValidator(prefixes);

        List<String> validUpperCaseCompanyNumbers = List.of("NI000000", "SC123123", "OC123123", "SO123123", "R0000000",
                "R123", "123", "AP123456");

        List<String> validLowerCaseCompanyNumbers = validUpperCaseCompanyNumbers.stream()
                .map(companyNumber -> companyNumber.toLowerCase()).collect(Collectors.toList());

        boolean isAllValid = Stream.of(validUpperCaseCompanyNumbers, validLowerCaseCompanyNumbers)
                .flatMap(x -> x.stream()).map(value -> companyNumberValidator.isValid(value, null))
                .allMatch(result -> result == true);

        assertTrue(isAllValid);

    }

    @Test
    void shouldReturnFalseWhenValueIsNull() {
        assertFalse(new CompanyNumberValidator("A,B,C").isValid(null, null));
    }

}