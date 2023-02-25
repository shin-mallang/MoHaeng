package com.mohaeng.notification.domain.model;

import com.mohaeng.notification.mock.MockNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("NotificationMakeStrategy(알림 생성 전략) 은")
class NotificationMakeStrategyTest {

    @Test
    void 처리할_수_있는_알림의_종류를_가진다() {
        // given
        NotificationMakeStrategy<MockNotificationEvent> notificationMakeStrategy = new NotificationMakeStrategy<>() {
            @Override
            protected List<Notification> makeNotifications(final MockNotificationEvent event) {
                return null;
            }
        };

        // when & then
        assertThat(notificationMakeStrategy.supportEvent()).isEqualTo(MockNotificationEvent.class);
    }
}