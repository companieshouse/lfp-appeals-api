package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;


import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
public class IllnessReasonMapperTest {
    private final IllnessReasonMapper mapper = new IllnessReasonMapper(new AttachmentMapper());
    private final static String ILL_PERSON = "name";
    private final static String OTHER_PERSON = "otherPersonName";
    private final static String ILLNESS_START = "01/01/2021";
    private final static boolean CONTINUED_ILLNESS = true;
    private final static String ILLNESS_END ="02/02/2021";
    private final static String FURTHER_INFORMATION ="furtherInformation";


        @Test
        void shouldReturnNullWhenValueIsNullReasonToEntity() {
            assertNull(mapper.map((IllnessReason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNullReasonToEntity() {
            IllnessReasonEntity mapped = mapper.map(new IllnessReason(ILL_PERSON, OTHER_PERSON, ILLNESS_START, CONTINUED_ILLNESS, ILLNESS_END, FURTHER_INFORMATION, Collections.emptyList()));
            assertEquals(ILL_PERSON, mapped.getIllPerson());
            assertEquals(OTHER_PERSON, mapped.getOtherPerson());
            assertEquals(ILLNESS_START, mapped.getIllnessStartDate());
            assertEquals(CONTINUED_ILLNESS, mapped.getContinuedIllness());
            assertEquals(ILLNESS_END, mapped.getIllnessEndDate());
            assertEquals(FURTHER_INFORMATION, mapped.getIllnessImpactFurtherInformation());
            assertTrue(mapped.getAttachments().isEmpty());
        }
        @Test
        void shouldReturnNullWhenValueIsNullEntityToReason() {
            assertNull(mapper.map((IllnessReasonEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNullEntityToReason() {
            IllnessReason mapped = mapper.map(new IllnessReasonEntity(ILL_PERSON, OTHER_PERSON, ILLNESS_START, CONTINUED_ILLNESS, ILLNESS_END, FURTHER_INFORMATION, Collections.emptyList()));
            assertEquals(ILL_PERSON, mapped.getIllPerson());
            assertEquals(OTHER_PERSON, mapped.getOtherPerson());
            assertEquals(ILLNESS_START, mapped.getIllnessStart());
            assertEquals(CONTINUED_ILLNESS, mapped.getContinuedIllness());
            assertEquals(ILLNESS_END, mapped.getIllnessEnd());
            assertEquals(FURTHER_INFORMATION, mapped.getIllnessImpactFurtherInformation());
            assertTrue(mapped.getAttachments().isEmpty());
        }
}

