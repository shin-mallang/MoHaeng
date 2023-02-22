package com.mohaeng.notification.mock;

import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockNotificationConfig {

    @Bean
    NotificationMakeStrategy mockNotificationMakeStrategy() {
        return new MockNotificationMakeStrategy();
    }
}
