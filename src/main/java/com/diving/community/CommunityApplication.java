package com.diving.community;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableDiscoveryClient
@SpringBootApplication
public class CommunityApplication {
    private static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application.yml,"
            + "classpath:aws.yml,"
            + "classpath:database.yml,"
            + "classpath:kafka.yml,"
            + "classpath:aws.yml,"
            + "/home/ubuntu/config/project/pungdong/database.yml,"
            + "/home/ubuntu/config/project/pungdong/kafka.yml,"
            + "/home/ubuntu/config/project/pungdong/aws.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(CommunityApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }
}
