package com.mohaeng.infrastructure.persistence.database.config;

import com.mohaeng.infrastructure.persistence.database.Persistence;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackageClasses = {Persistence.class}
)
public class JpaConfiguration {

}
