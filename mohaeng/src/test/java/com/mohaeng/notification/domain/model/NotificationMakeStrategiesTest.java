package com.mohaeng.notification.domain.model;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.mock.MockNotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("NotificationMakeStrategies 은")
class NotificationMakeStrategiesTest {

    private NotificationMakeStrategies notificationMakeStrategies;

    @Test
    void 이벤트에_대응되는_알림_생성_전략이_있다면_알림을_생성한다() {
        // given
        NotificationMakeStrategy<MockNotificationEvent> mockStrategy = new NotificationMakeStrategy<>() {
            @Override
            protected List<Notification> makeNotifications(final MockNotificationEvent event) {
                Notification mock = mock(Notification.class);
                return event.receiverIds().stream().map(it -> mock).toList();
            }
        };
        List<Long> receiverIds = List.of(1L, 2L, 3L);
        notificationMakeStrategies = new NotificationMakeStrategies(Set.of(mockStrategy));
        MockNotificationEvent mockNotificationEvent = new MockNotificationEvent(this, receiverIds);

        // when
        List<Notification> make = notificationMakeStrategies.make(mockNotificationEvent);

        // then
        assertThat(make.size()).isEqualTo(receiverIds.size());
    }

    @Test
    void 이벤트에_대응되는_알림_생성_전략이_없다면_오류를_발생시킨다() {
        // when & then
        assertThatThrownBy(() -> notificationMakeStrategies.make(mock(NotificationEvent.class)))
                .isInstanceOf(NullPointerException.class);
    }

}