package uk.gov.companieshouse.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.gov.companieshouse.config.CompanyNumberConfiguration;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.service.CompanyNumberRegexFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.TestData.Appeal.PenaltyIdentifier.companyNumber;
import static uk.gov.companieshouse.TestData.Appeal.PenaltyIdentifier.penaltyReference;

@ExtendWith(SpringExtension.class)
public class PenaltyIdentifierMapperTest {

    @Mock
    private CompanyNumberConfiguration companyNumberConfiguration;

    private CompanyNumberRegexFactory companyNumberRegexFactory;

    private PenaltyIdentifierMapper mapper;

    @BeforeEach
    void beforeEach() {
        when(companyNumberConfiguration.getPrefixes()).thenReturn("SC,NI,OC,SO,R,AP");
        this.companyNumberRegexFactory = new CompanyNumberRegexFactory(companyNumberConfiguration);
        this.mapper = new PenaltyIdentifierMapper(companyNumberRegexFactory);
    }

    @Nested
    class ToEntityMappingTest {

        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((PenaltyIdentifier) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            PenaltyIdentifierEntity mapped = mapper.map(new PenaltyIdentifier(companyNumber, penaltyReference));
            assertEquals(companyNumber, mapped.getCompanyNumber());
            assertEquals(penaltyReference, mapped.getPenaltyReference());
        }
    }

    @Nested
    class FromEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((PenaltyIdentifierEntity) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            PenaltyIdentifier mapped = mapper.map(new PenaltyIdentifierEntity(companyNumber, penaltyReference));
            assertEquals(companyNumber, mapped.getCompanyNumber());
            assertEquals(penaltyReference, mapped.getPenaltyReference());
        }
    }
}
