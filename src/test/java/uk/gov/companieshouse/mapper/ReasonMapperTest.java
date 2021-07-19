package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.continuedIllness;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.illPerson;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.illnessEnd;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.illnessStart;
import static uk.gov.companieshouse.TestData.Appeal.Reason.IllnessReason.otherPerson;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.description;
import static uk.gov.companieshouse.TestData.Appeal.Reason.OtherReason.title;
import static uk.gov.companieshouse.util.TestUtil.createIllnessReason;

import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
class ReasonMapperTest {
    private final ReasonMapper mapper = new ReasonMapper(new OtherReasonMapper(new AttachmentMapper()),
        new IllnessReasonMapper(new AttachmentMapper()));

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Reason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            Reason reason = new Reason();
            reason.setIllnessReason(new IllnessReason(illPerson, otherPerson, illnessStart, continuedIllness,
                illnessEnd, illnessImpactFurtherInformation, Collections.emptyList()));
            reason.setOther(new OtherReason(title, description, Collections.emptyList()));

            ReasonEntity mapped = mapper.map(reason);
            assertEquals(title, mapped.getOther().getTitle());
            assertEquals(description, mapped.getOther().getDescription());
            assertEquals(illPerson, mapped.getIllnessReason().getIllPerson());
            assertEquals(otherPerson, mapped.getIllnessReason().getOtherPerson());
            assertEquals(illnessStart, mapped.getIllnessReason().getIllnessStartDate());
            assertEquals(continuedIllness, mapped.getIllnessReason().getContinuedIllness());
            assertEquals(illnessEnd, mapped.getIllnessReason().getIllnessEndDate());
            assertEquals(illnessImpactFurtherInformation,
                mapped.getIllnessReason().getIllnessImpactFurtherInformation());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((ReasonEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            ReasonEntity reasonEntity = new ReasonEntity();
            reasonEntity.setIllnessReason(new IllnessReasonEntity(illPerson, otherPerson, illnessStart,
                continuedIllness, illnessEnd, illnessImpactFurtherInformation, Collections.emptyList()));
            reasonEntity.setOther(new OtherReasonEntity(title, description, Collections.emptyList()));

            Reason mapped = mapper.map(reasonEntity);
            assertEquals(title, mapped.getOther().getTitle());
            assertEquals(description, mapped.getOther().getDescription());
            assertEquals(illPerson, mapped.getIllnessReason().getIllPerson());
            assertEquals(otherPerson, mapped.getIllnessReason().getOtherPerson());
            assertEquals(illnessStart, mapped.getIllnessReason().getIllnessStart());
            assertEquals(continuedIllness, mapped.getIllnessReason().getContinuedIllness());
            assertEquals(illnessEnd, mapped.getIllnessReason().getIllnessEnd());
            assertEquals(illnessImpactFurtherInformation,
                mapped.getIllnessReason().getIllnessImpactFurtherInformation());
        }
    }
}
