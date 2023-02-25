package com.mohaeng.notification.domain.model;

import com.mohaeng.notification.application.dto.NotificationDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Notification(알림) 은")
class NotificationTest {

    @Test
    void 알림은_읽을_수_있다() {
        // given
        Notification notification = new Notification() {
            public NotificationDto toDto() {
                return null;
            }
        };
        Assertions.assertThat(notification.isRead()).isFalse();

        // when
        notification.read();

        // then
        Assertions.assertThat(notification.isRead()).isTrue();
    }
}