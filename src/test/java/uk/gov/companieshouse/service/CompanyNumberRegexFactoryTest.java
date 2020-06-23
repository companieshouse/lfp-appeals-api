package uk.gov.companieshouse.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.companieshouse.config.CompanyNumberConfiguration;

@ExtendWith(SpringExtension.class)
public class CompanyNumberRegexFactoryTest {

    @Test
    void shouldThrowAnExceptionWhenInputListIsInWrongFormat() {

        List<String> invalidLists = List.of(",", "A,[.", "A.BC.D");

        CompanyNumberConfiguration config = mock(CompanyNumberConfiguration.class);

        invalidLists.forEach(prefixList -> {
            when(config.getPrefixes()).thenReturn(prefixList);
            assertThrows(IllegalArgumentException.class, () -> new CompanyNumberRegexFactory(config), prefixList);
        });

    }

    @Test
    void shouldCreateFactorySuccessfulyWhenValidListsProvided() {
        List<String> validLists = List.of("NI", "AB,CD,EF", "A,B,C,D,F", "AB,cd,e,F");
        CompanyNumberConfiguration config = mock(CompanyNumberConfiguration.class);

        validLists.forEach(prefixList -> {
            when(config.getPrefixes()).thenReturn(prefixList);
            assertDoesNotThrow(() -> new CompanyNumberRegexFactory(config), prefixList);
        });
    }

    @Test
    void shouldReturnFalseWhenPrefixIsNotAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberConfiguration config = mock(CompanyNumberConfiguration.class);
        when(config.getPrefixes()).thenReturn(prefixes);

        CompanyNumberRegexFactory companyNumberRegexFactory = new CompanyNumberRegexFactory(config);

        List<String> invalidCompanyNumbers = List.of("TY000001", "PTABC0123", "XC999999", "T0000001");

        boolean isAllInvalid = invalidCompanyNumbers.stream().map(companyNumberRegexFactory::matchCompanyNumber)
                .allMatch(result -> result == false);

        assertTrue(isAllInvalid);

    }

    @Test
    void shouldReturnTrueWhenPrefixesAreAllowed() {
        String prefixes = "SC,NI,OC,SO,R,AP";

        CompanyNumberConfiguration config = mock(CompanyNumberConfiguration.class);
        when(config.getPrefixes()).thenReturn(prefixes);

        CompanyNumberRegexFactory companyNumberRegexFactory = new CompanyNumberRegexFactory(config);

        List<String> validUpperCaseCompanyNumbers = List.of("NI000000", "SC123123", "OC123123", "SO123123", "R0000000",
                "R123", "123", "AP123456");

        List<String> validLowerCaseCompanyNumbers = validUpperCaseCompanyNumbers.stream()
                .map(companyNumber -> companyNumber.toLowerCase()).collect(Collectors.toList());

        boolean isAllValid = Stream.of(validUpperCaseCompanyNumbers, validLowerCaseCompanyNumbers)
                .flatMap(x -> x.stream()).map(companyNumberRegexFactory::matchCompanyNumber)
                .allMatch(result -> result == true);

        assertTrue(isAllValid);

    }

}