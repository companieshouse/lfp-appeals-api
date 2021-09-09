package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.database.entity.IllnessReasonEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(MockitoExtension.class)
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

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Reason) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            when(illnessReasonMapper.map(any(IllnessReason.class))).thenReturn(illnessReasonEntity);
            when(otherReasonMapper.map(any(OtherReason.class))).thenReturn(otherReasonEntity);

            Reason reason = new Reason();
            reason.setOther(mockOtherReason);
            reason.setIllness(mockIllnessReason);

            ReasonEntity mapped = mapper.map(reason);

            assertEquals(illnessReasonEntity, mapped.getIllnessReason());
            assertEquals(otherReasonEntity, mapped.getOther());

            verify(illnessReasonMapper).map(mockIllnessReason);
            verify(otherReasonMapper).map(mockOtherReason);
        }
    }

    @Nested
    class FromEntityMappingTest {

        @DisplayName("Should return null when value is null")
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((ReasonEntity) null));
        }

        @DisplayName("Should map value when value is not null")
        @Test
        void shouldMapValueWhenValueIsNotNull() {
            when(illnessReasonMapper.map(any(IllnessReasonEntity.class))).thenReturn(mockIllnessReason);
            when(otherReasonMapper.map(any(OtherReasonEntity.class))).thenReturn(mockOtherReason);

            ReasonEntity reasonEntity = new ReasonEntity();

            reasonEntity.setIllnessReason(illnessReasonEntity);
            reasonEntity.setOther(otherReasonEntity);

            Reason mapped = mapper.map(reasonEntity);

            assertEquals(mockIllnessReason, mapped.getIllness());
            assertEquals(mockOtherReason, mapped.getOther());

            verify(illnessReasonMapper).map(illnessReasonEntity);
            verify(otherReasonMapper).map(otherReasonEntity);
        }
    }
}
