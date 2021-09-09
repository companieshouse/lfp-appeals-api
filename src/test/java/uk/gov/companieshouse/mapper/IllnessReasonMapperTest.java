package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.TestUtil;


@ExtendWith(MockitoExtension.class)
class IllnessReasonMapperTest {

    @Spy
    private AttachmentMapper attachmentMapper;

    @InjectMocks
    private IllnessReasonMapper mapper;

    @DisplayName("Should return null when value is null reason to entity")
    @Test
    void shouldReturnNullWhenValueIsNullReasonToEntity() {
        assertNull(mapper.map((IllnessReason) null));
    }

    @DisplayName("Should map value when value is not null reason to entity")
    @Test
    void shouldMapValueWhenValueIsNotNullReasonToEntity() {

        IllnessReasonEntity mapped = mapper.map(TestUtil.createIllnessReason());
        assertEquals(TestData.ILL_PERSON, mapped.getIllPerson());
        assertEquals(TestData.OTHER_PERSON, mapped.getOtherPerson());
        assertEquals(TestData.ILLNESS_START, mapped.getIllnessStartDate());
        assertEquals(TestData.CONTINUED_ILLNESS, mapped.getContinuedIllness());
        assertEquals(TestData.ILLNESS_END, mapped.getIllnessEndDate());
        assertEquals(TestData.ILLNESS_IMPACT_FURTHER_INFORMATION, mapped.getIllnessImpactFurtherInformation());

        assertEquals(1, mapped.getAttachments().size());
        assertEquals(TestData.ATTACHMENT_ID, mapped.getAttachments().get(0).getId());
        assertEquals(TestData.ATTACHMENT_NAME, mapped.getAttachments().get(0).getName());
        assertEquals(TestData.ATTACHMENT_SIZE, mapped.getAttachments().get(0).getSize());
        assertEquals(TestData.CONTENT_TYPE, mapped.getAttachments().get(0).getContentType());

    }

    @DisplayName("Should return null when valuse is null entity to reason")
    @Test
    void shouldReturnNullWhenValueIsNullEntityToReason() {
        assertNull(mapper.map((IllnessReasonEntity) null));
    }

    @DisplayName("Should map value when value is not null entity to reason")
    @Test
    void shouldMapValueWhenValueIsNotNullEntityToReason() {
        IllnessReason mapped = mapper.map(TestUtil.createIllnessReasonEntity());
        assertEquals(TestData.ILL_PERSON, mapped.getIllPerson());
        assertEquals(TestData.OTHER_PERSON, mapped.getOtherPerson());
        assertEquals(TestData.ILLNESS_START, mapped.getIllnessStart());
        assertEquals(TestData.CONTINUED_ILLNESS, mapped.getContinuedIllness());
        assertEquals(TestData.ILLNESS_END, mapped.getIllnessEnd());
        assertEquals(TestData.ILLNESS_IMPACT_FURTHER_INFORMATION, mapped.getIllnessImpactFurtherInformation());

        assertEquals(1, mapped.getAttachments().size());
        assertEquals(TestData.ATTACHMENT_ID, mapped.getAttachments().get(0).getId());
        assertEquals(TestData.ATTACHMENT_NAME, mapped.getAttachments().get(0).getName());
        assertEquals(TestData.ATTACHMENT_SIZE, mapped.getAttachments().get(0).getSize());
        assertEquals(TestData.CONTENT_TYPE, mapped.getAttachments().get(0).getContentType());

    }
}

