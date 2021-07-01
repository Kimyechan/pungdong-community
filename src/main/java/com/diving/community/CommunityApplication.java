package com.diving.community;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CommunityApplication {
    private static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "classpath:database.yml,"
            + "classpath:kafka.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(CommunityApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

}
