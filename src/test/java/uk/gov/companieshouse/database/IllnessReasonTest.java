package uk.gov.companieshouse.database;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.database.entity.AttachmentEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

class IllnessReasonTest {
    private static final String illPerson = "Someone";
    private static final String otherPerson = "director";
    private static final String illnessStart = "12/10/2020";
    private static final String continuedIllness = "yes";
    private static final String illnessEnd = "10/12/2021";
    private static final String illnessImpactFurtherInformation = "some further info";
    private static final List<AttachmentEntity> attachments = new ArrayList<>();

    @Test
    void illnessResasonTest() {
        final IllnessReason illnessReason = new IllnessReason(illPerson, otherPerson, illnessStart, continuedIllness,
            illnessEnd, illnessImpactFurtherInformation, attachments);

        assertEquals(illPerson, illnessReason.getIllPerson());
        assertEquals(otherPerson, illnessReason.getOtherPerson());
        assertEquals(illnessStart, illnessReason.getIllnessStart());
        assertEquals(continuedIllness, illnessReason.getContinuedIllness());
        assertEquals(illnessEnd, illnessReason.getIllnessEnd());
        assertEquals(illnessImpactFurtherInformation, illnessReason.getIllnessImpactFurtherInformation());
        assertEquals(attachments, illnessReason.getAttachments());
    }
}
