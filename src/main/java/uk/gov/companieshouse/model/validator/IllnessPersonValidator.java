package uk.gov.companieshouse.model.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Appeal;

import java.util.Arrays;

@Component
public class IllnessPersonValidator {

    static final String ILL_PERSON_INVALID = "Invalid data. IllPerson is not valid";
    static final String OTHER_PERSON_EMPTY = "Invalid data. Other person should Not be empty";
    static final String OTHER_PERSON_INVALID = "Invalid data. Other person should be empty";

    private static final String[] illPersonList = { "director", "accountant", "family", "employee", "someoneElse" };

    public String validateIllnessPerson(Appeal appeal) {
        String illPerson = appeal.getReason().getIllness().getIllPerson();
        String otherPerson = appeal.getReason().getIllness().getOtherPerson();

        boolean isSomeoneElse = illPerson.equals("someoneElse");
        boolean isOtherPersonEmpty = otherPerson == null || otherPerson.isBlank();

        if(!Arrays.asList(illPersonList).contains(illPerson)){
            return ILL_PERSON_INVALID;
        } else if ( isSomeoneElse && isOtherPersonEmpty) {
            return OTHER_PERSON_EMPTY;
        } else if ( !isSomeoneElse && !isOtherPersonEmpty ) {
            return OTHER_PERSON_INVALID;
        }

        return null;
    }
}
