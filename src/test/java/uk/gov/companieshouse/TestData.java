package uk.gov.companieshouse;

import java.time.LocalDateTime;

public interface TestData {
    interface Appeal {
        String id = "APPEAL#99";
        LocalDateTime createdAt = LocalDateTime.of(2010, 12, 31, 23, 59);
        interface CreatedBy {
            String id = "USER#1";
        }
        interface PenaltyIdentifier {
            String companyNumber = "12345678";
            String penaltyReference = "A12345678";
        }
        interface Reason {
            interface OtherReason {
                String title = "Some title";
                String description = "Some description";
            }
            interface Attachment {
                String id = "FILE#1";
                String name = "file.txt";
                String contentType = "plain/text";
                int size = 100;
            }
        }
    }
}
