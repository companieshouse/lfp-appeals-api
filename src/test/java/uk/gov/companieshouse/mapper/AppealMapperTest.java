package uk.gov.companieshouse.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.CreatedBy;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;

@ExtendWith(MockitoExtension.class)
class AppealMapperTest {
    @Mock
    private ReasonMapper reasonMapper;
    @Mock
    private PenaltyIdentifierMapper penaltyIdentifierMapper;
    @Mock
    private CreatedByMapper createdByMapper;

    @Mock
    private ReasonEntity reasonEntity;
    @Mock
    private PenaltyIdentifierEntity penaltyIdentifierEntity;
    @Mock
    private CreatedByEntity mockCreatedByEntity;

    @Mock
    private Reason mockReason;
    @Mock
    private PenaltyIdentifier mockPenaltyIdentifier;
    @Mock
    private CreatedBy mockCreatedBy;

    @InjectMocks
    private AppealMapper mapper;

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Appeal) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            when(createdByMapper.map(any(CreatedBy.class))).thenReturn(null);
            when(penaltyIdentifierMapper.map(any(PenaltyIdentifier.class))).thenReturn(penaltyIdentifierEntity);
            when(reasonMapper.map(any(Reason.class))).thenReturn(reasonEntity);

            AppealEntity mapped = mapper.map(new Appeal(
                null,
                null,
                mockCreatedBy,
                mockPenaltyIdentifier,
                mockReason
            ));

            assertNull(mapped.getId());
            assertNull(mapped.getCreatedAt());
            assertNull(mapped.getCreatedBy());
            assertEquals(penaltyIdentifierEntity, mapped.getPenaltyIdentifier());
            assertEquals(reasonEntity, mapped.getReason());

            verify(createdByMapper).map(mockCreatedBy);
            verify(penaltyIdentifierMapper).map(mockPenaltyIdentifier);
            verify(reasonMapper).map(mockReason);
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((AppealEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            when(createdByMapper.map(any(CreatedByEntity.class))).thenReturn(mockCreatedBy);
            when(penaltyIdentifierMapper.map(any(PenaltyIdentifierEntity.class))).thenReturn(mockPenaltyIdentifier);
            when(reasonMapper.map(any(ReasonEntity.class))).thenReturn(mockReason);

            Appeal mapped = mapper.map(new AppealEntity(
                TestData.ID,
                TestData.CREATED_AT,
                mockCreatedByEntity,
                penaltyIdentifierEntity,
                reasonEntity
            ));

            assertEquals(TestData.ID, mapped.getId());
            assertEquals(TestData.CREATED_AT, mapped.getCreatedAt());

            assertEquals(mockCreatedBy, mapped.getCreatedBy());
            assertEquals(mockPenaltyIdentifier, mapped.getPenaltyIdentifier());
            assertEquals(mockReason, mapped.getReason());

            verify(createdByMapper).map(mockCreatedByEntity);
            verify(penaltyIdentifierMapper).map(penaltyIdentifierEntity);
            verify(reasonMapper).map(reasonEntity);
        }
    }
}
