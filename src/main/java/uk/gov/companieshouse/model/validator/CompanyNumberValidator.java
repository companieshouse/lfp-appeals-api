package uk.gov.companieshouse.model.validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CompanyNumberValidator implements ConstraintValidator<ValidCompanyNumber, String> {

    private final Pattern prefixListRegex = Pattern.compile("(?i)^([A-Z][A-Z]?)\\b(,[A-Z][A-Z]?)*$");

    private final Pattern companyNumberRegex;

    public CompanyNumberValidator(@Value("${companyNumber.prefixes}") String prefixString) {

        if (!this.prefixListRegex.matcher(prefixString).matches()) {
            throw new IllegalArgumentException(
                    "Prefix list formatting error. Make sure list is comma separated e.g. NI,SI,R. Current: "
                            + prefixString);
        }

        this.companyNumberRegex = Pattern.compile("(?i)^(" + String.join("|", generatePrefixList(prefixString)) + ")$");

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return this.companyNumberRegex.matcher(value).matches();
    }

    private List<String> generatePrefixList(String prefixString) {
        List<String> prefixesArray = Arrays.asList(prefixString.split(","));

        String singleCharacterPrefixRegex = "("
                .concat(createPrefixSubstring(prefixesArray, 1).concat(")").concat("[0-9]{1,7}"));

        String doubleCharacterPrefixRegex = "("
                .concat(createPrefixSubstring(prefixesArray, 2).concat(")").concat("[0-9]{1,6}"));

        String onlyNumbersRegex = "[0-9]{1,8}";

        return List.of(singleCharacterPrefixRegex, doubleCharacterPrefixRegex, onlyNumbersRegex);
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