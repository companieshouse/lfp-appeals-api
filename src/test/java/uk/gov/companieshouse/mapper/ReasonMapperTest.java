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
public class ReasonMapperTest {
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
            ReasonEntity mapped = mapper.map(new Reason(new OtherReason(title, description, Collections.emptyList()),
                new IllnessReason(illPerson, otherPerson, illnessStart, continuedIllness, illnessEnd,
                    illnessImpactFurtherInformation, Collections.emptyList())));
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
            Reason mapped = mapper.map(
                new ReasonEntity(new OtherReasonEntity(title, description, Collections.emptyList()),
                    new IllnessReasonEntity(illPerson, otherPerson, illnessStart, continuedIllness, illnessEnd,
                        illnessImpactFurtherInformation, Collections.emptyList())));
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
