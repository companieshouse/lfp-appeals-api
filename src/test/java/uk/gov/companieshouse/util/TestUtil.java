package uk.gov.companieshouse.util;

import org.assertj.core.util.Lists;
import uk.gov.companieshouse.TestData;
import uk.gov.companieshouse.model.Attachment;
import uk.gov.companieshouse.model.IllnessReason;
import uk.gov.companieshouse.model.OtherReason;

public class TestUtil {
    public static IllnessReason createIllnessReason() {
        return new IllnessReason(TestData.Appeal.Reason.IllnessReason.illPerson,
            TestData.Appeal.Reason.IllnessReason.otherPerson, TestData.Appeal.Reason.IllnessReason.illnessStart,
            TestData.Appeal.Reason.IllnessReason.continuedIllness, TestData.Appeal.Reason.IllnessReason.illnessEnd,
            TestData.Appeal.Reason.IllnessReason.illnessImpactFurtherInformation, Lists.newArrayList(
            new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size,
                TestData.Appeal.Reason.Attachment.url),
            new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size, null)));
    }

    public static OtherReason createOtherReason() {
        return new OtherReason(TestData.Appeal.Reason.OtherReason.title, TestData.Appeal.Reason.OtherReason.description,
            Lists.newArrayList(
                new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size,
                    TestData.Appeal.Reason.Attachment.url),
                new Attachment(TestData.Appeal.Reason.Attachment.id, TestData.Appeal.Reason.Attachment.name,
                    TestData.Appeal.Reason.Attachment.contentType, TestData.Appeal.Reason.Attachment.size, null)));
    }
}
