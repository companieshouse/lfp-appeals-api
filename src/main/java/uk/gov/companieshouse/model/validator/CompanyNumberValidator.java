package uk.gov.companieshouse.model.validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.AppealApplication;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class CompanyNumberValidator implements ConstraintValidator<ValidCompanyNumber, String> {

    private final Pattern companyNumberRegex;

    public CompanyNumberValidator(@Value("${companyNumber.prefixes}") String prefixString) {

        Pattern prefixListRegex = Pattern.compile("(?i)^([A-Z][A-Z]?)\\b(,[A-Z][A-Z]?)*$");

        if (!prefixListRegex.matcher(prefixString).matches()) {
            throw new IllegalArgumentException(
                    "Prefix list formatting error. Make sure list is comma separated e.g. NI,SI,R. Current: "
                            + prefixString);
        }

        String regexString = "(?i)^(" + String.join("|", generatePrefixList(prefixString)) + ")$";

        LoggerFactory.getLogger(AppealApplication.APPLICATION_NAME_SPACE).debug(regexString);

        this.companyNumberRegex = Pattern.compile(regexString);

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }
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

    private String removeFirstCharacterOf(String value) {
        if (value.length() <= 1) {
            return value;
        }

        return value.substring(1);
    }

    private String createPrefixSubstring(List<String> prefixesArray, int numberOfLetters) {
        return removeFirstCharacterOf(prefixesArray.stream().filter(prefix -> prefix.length() == numberOfLetters)
                .reduce("", (p, c) -> p + "|" + c));
    }

}
