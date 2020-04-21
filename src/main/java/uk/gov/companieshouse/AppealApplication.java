package uk.gov.companieshouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppealApplication {

    public static void main (String[] args) {
        int target = -5;
        int num = 3;

        target =- num;  // Noncompliant; target = -3. Is that really what's meant?
        target =+ num; // Noncompliant; target = 3
        System.out.println("target = " + target);

        String.format("The value of my integer is %d", "Hello World");
        SpringApplication.run(AppealApplication.class, args);
    }
}
