package uk.gov.companieshouse.service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import uk.gov.companieshouse.config.CompanyNumberConfiguration;

@Component
public class CompanyNumberRegexFactory {

    private final Pattern prefixListRegex = Pattern.compile("(?i)^([A-Z][A-Z]?)\\b(,[A-Z][A-Z]?)*$");

    private final Pattern companyNumberRegex;

    public CompanyNumberRegexFactory(CompanyNumberConfiguration prefixConfig) {

        String prefixString = prefixConfig.getPrefixes();

        if (!this.prefixListRegex.matcher(prefixString).matches()) {
            throw new IllegalArgumentException(
                    "Prefix list formatting error. Make sure list is comma separated e.g. NI,SI,R. Current: "
                            + prefixString);
        }

        List<String> prefixesArray = Arrays.asList(prefixString.split(","));

        String singleCharacterPrefixRegex = "("
                .concat(createPrefixSubstring(prefixesArray, 1).concat(")").concat("[0-9]{1,7}"));

        String doubleCharacterPrefixRegex = "("
                .concat(createPrefixSubstring(prefixesArray, 2).concat(")").concat("[0-9]{1,6}"));

        String onlyNumbersRegex = "[0-9]{1,8}";

        this.companyNumberRegex = Pattern.compile("(?i)^("
                + String.join("|", List.of(singleCharacterPrefixRegex, doubleCharacterPrefixRegex, onlyNumbersRegex))
                + ")$");

    }

    public boolean matchCompanyNumber(String companyNumber) {
        return this.companyNumberRegex.matcher(companyNumber).matches();
    }

    private String safeSubstring(String value, int startIndex) {
        if (startIndex > value.length()) {
            return value;
        }

        return value.substring(startIndex);
    }

    private String createPrefixSubstring(List<String> prefixesArray, int numberOfLetters) {
        return safeSubstring(prefixesArray.stream().filter(prefix -> prefix.length() == numberOfLetters).reduce("",
                (p, c) -> p + "|" + c), 1);
    }

}