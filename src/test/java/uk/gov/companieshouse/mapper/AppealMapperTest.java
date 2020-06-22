package uk.gov.companieshouse.mapper;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.database.entity.AppealEntity;
import uk.gov.companieshouse.database.entity.AttachmentEntity;
import uk.gov.companieshouse.database.entity.CreatedByEntity;
import uk.gov.companieshouse.database.entity.OtherReasonEntity;
import uk.gov.companieshouse.database.entity.PenaltyIdentifierEntity;
import uk.gov.companieshouse.database.entity.ReasonEntity;
import uk.gov.companieshouse.model.Appeal;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.OtherReason;
import uk.gov.companieshouse.model.PenaltyIdentifier;
import uk.gov.companieshouse.model.Reason;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
public class AppealMapperTest {
    private final AppealMapper mapper = new AppealMapper(
        new CreatedByMapper(),
        new PenaltyIdentifierMapper(),
        new ReasonMapper(new OtherReasonMapper(new AttachmentMapper()))
    );

    @Nested
    class ToEntityMappingTest {
        @Test
        void shouldReturnNullWhenValueIsNull() {
            assertNull(mapper.map((Appeal) null));
        }

        @Test
        void shouldMapValueWhenValueIsNotNull() {
            AppealEntity mapped = mapper.map(new Appeal(
                null,
                null,
                null,
                new PenaltyIdentifier(
                    TestData.Appeal.PenaltyIdentifier.companyNumber,
                    TestData.Appeal.PenaltyIdentifier.penaltyReference
                ),
                new Reason(
                    new OtherReason(
                        TestData.Appeal.Reason.OtherReason.title,
                        TestData.Appeal.Reason.OtherReason.description,
                        Lists.newArrayList(new Attachment(
                            TestData.Appeal.Reason.Attachment.id,
                            TestData.Appeal.Reason.Attachment.name,
                            TestData.Appeal.Reason.Attachment.contentType,
                            TestData.Appeal.Reason.Attachment.size,
                            null
                        ))
                    )
                )
            ));
            assertNull(mapped.getId());
            assertNull(mapped.getCreatedAt());
            assertNull(mapped.getCreatedBy());
            assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, mapped.getPenaltyIdentifier().getCompanyNumber());
            assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference, mapped.getPenaltyIdentifier().getPenaltyReference());
            assertEquals(TestData.Appeal.Reason.OtherReason.title, mapped.getReason().getOther().getTitle());
            assertEquals(TestData.Appeal.Reason.OtherReason.description, mapped.getReason().getOther().getDescription());
            assertEquals(TestData.Appeal.Reason.Attachment.id, mapped.getReason().getOther().getAttachments().get(0).getId());
            assertEquals(TestData.Appeal.Reason.Attachment.name, mapped.getReason().getOther().getAttachments().get(0).getName());
            assertEquals(TestData.Appeal.Reason.Attachment.contentType, mapped.getReason().getOther().getAttachments().get(0).getContentType());
            assertEquals(TestData.Appeal.Reason.Attachment.size, mapped.getReason().getOther().getAttachments().get(0).getSize());
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
            Appeal mapped = mapper.map(new AppealEntity(
                TestData.Appeal.id,
                TestData.Appeal.createdAt,
                new CreatedByEntity(
                    TestData.Appeal.CreatedBy.id
                ),
                new PenaltyIdentifierEntity(
                    TestData.Appeal.PenaltyIdentifier.companyNumber,
                    TestData.Appeal.PenaltyIdentifier.penaltyReference
                ),
                new ReasonEntity(
                    new OtherReasonEntity(
                        TestData.Appeal.Reason.OtherReason.title,
                        TestData.Appeal.Reason.OtherReason.description,
                        Lists.newArrayList(new AttachmentEntity(
                            TestData.Appeal.Reason.Attachment.id,
                            TestData.Appeal.Reason.Attachment.name,
                            TestData.Appeal.Reason.Attachment.contentType,
                            TestData.Appeal.Reason.Attachment.size
                        ))
                    )
                )
            ));
            assertEquals(TestData.Appeal.id, mapped.getId());
            assertEquals(TestData.Appeal.createdAt, mapped.getCreatedAt());
            assertEquals(TestData.Appeal.CreatedBy.id, mapped.getCreatedBy().getId());
            assertEquals(TestData.Appeal.PenaltyIdentifier.companyNumber, mapped.getPenaltyIdentifier().getCompanyNumber());
            assertEquals(TestData.Appeal.PenaltyIdentifier.penaltyReference, mapped.getPenaltyIdentifier().getPenaltyReference());
            assertEquals(TestData.Appeal.Reason.OtherReason.title, mapped.getReason().getOther().getTitle());
            assertEquals(TestData.Appeal.Reason.OtherReason.description, mapped.getReason().getOther().getDescription());
            assertEquals(TestData.Appeal.Reason.Attachment.id, mapped.getReason().getOther().getAttachments().get(0).getId());
            assertEquals(TestData.Appeal.Reason.Attachment.name, mapped.getReason().getOther().getAttachments().get(0).getName());
            assertEquals(TestData.Appeal.Reason.Attachment.contentType, mapped.getReason().getOther().getAttachments().get(0).getContentType());
            assertEquals(TestData.Appeal.Reason.Attachment.size, mapped.getReason().getOther().getAttachments().get(0).getSize());
        }
    }
}
