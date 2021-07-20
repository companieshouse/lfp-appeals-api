package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(SpringExtension.class)
class ReasonMapperTest {

    @Mock
    private OtherReasonMapper otherReasonMapper;
    @Mock
    private OtherReasonEntity otherReasonEntity;

    @Mock
    private IllnessReasonMapper illnessReasonMapper;
    @Mock
    private IllnessReasonEntity illnessReasonEntity;

    @InjectMocks
    private ReasonMapper mapper;

    @Mock
    private IllnessReason mockIllnessReason;
    @Mock
    private OtherReason mockOtherReason;

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Reason) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            when(illnessReasonMapper.map(any(IllnessReason.class))).thenReturn(illnessReasonEntity);
            when(otherReasonMapper.map(any(OtherReason.class))).thenReturn(otherReasonEntity);

            Reason reason = new Reason();
            reason.setOther(mockOtherReason);
            reason.setIllnessReason(mockIllnessReason);

            ReasonEntity mapped = mapper.map(reason);

            assertEquals(illnessReasonEntity, mapped.getIllnessReason());
            assertEquals(otherReasonEntity, mapped.getOther());
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
            when(illnessReasonMapper.map(any(IllnessReasonEntity.class))).thenReturn(mockIllnessReason);
            when(otherReasonMapper.map(any(OtherReasonEntity.class))).thenReturn(mockOtherReason);

            ReasonEntity reasonEntity = new ReasonEntity();

            reasonEntity.setIllnessReason(illnessReasonEntity);
            reasonEntity.setOther(otherReasonEntity);

            Reason mapped = mapper.map(reasonEntity);

            assertEquals(mockIllnessReason, mapped.getIllnessReason());
            assertEquals(mockOtherReason, mapped.getOther());
        }
    }
}
