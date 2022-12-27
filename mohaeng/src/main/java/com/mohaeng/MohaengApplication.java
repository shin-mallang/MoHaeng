package com.mohaeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class MohaengApplication {

    public static void main(String[] args) {
        SpringApplication.run(MohaengApplication.class, args);
    }

}
