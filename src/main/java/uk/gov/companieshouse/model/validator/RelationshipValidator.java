package uk.gov.companieshouse.model.validator;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.model.Appeal;

@Component
public class RelationshipValidator {

    public String validateRelationship(Appeal appeal) {
        if(appeal.getCreatedBy().getRelationshipToCompany() != null && appeal.getReason().getIllness() != null) {
            appeal.getCreatedBy().setRelationshipToCompany(null);
            return null;
        }
        else if(appeal.getCreatedBy().getRelationshipToCompany() == null && appeal.getReason().getOther() != null) {
            return "Createdby.RelationshipToCompany must not be null when supplying Other Reason";
        }
        else return null;
    }
}
