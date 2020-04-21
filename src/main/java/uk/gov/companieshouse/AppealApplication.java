package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppealApplication {

    public static void main (String[] args) {
        for (int i = 0; i < 4; i++) {
            System.out.println("i = " + i);
        }
        SpringApplication.run(AppealApplication.class, args);
    }

    private static final int UPPER = 20;
    private static final int LOWER = 0;

    public int doRangeCheck(int num) {    // Let's say num = 12
        int result = Math.min(LOWER, num);  // result = 0
        return Math.max(UPPER, result);     // Noncompliant; result is now 20: even though 12 was in the range
    }
}
