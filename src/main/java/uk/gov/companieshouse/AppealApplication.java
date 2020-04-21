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
}
