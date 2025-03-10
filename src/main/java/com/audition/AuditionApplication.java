package com.audition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Audition application. This is the main class for the application, where the Spring Boot
 * application is launched.
 */
@SpringBootApplication
public class AuditionApplication {


    /**
     * Main method to launch the Audition application.
     *
     * @param args : String[]
     */
    public static void main(final String[] args) {
        SpringApplication.run(AuditionApplication.class, args);
    }

}
