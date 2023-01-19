package com.mohaeng.notification.application.eventhandler.strategy;

import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.applicationform.domain.event.ClubJoinApplicationCreatedEvent;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("ApplicationProcessedNotificationMakeStrategy 는 ")
class ApplicationProcessedNotificationMakeStrategyTest {

    private ApplicationProcessedNotificationMakeStrategy strategy = new ApplicationProcessedNotificationMakeStrategy();

    @Test
    @DisplayName("ApplicationProcessedEvent 이벤트만을 처리할 수 있다.")
    void success_test_1() {
        // then
        Assertions.assertAll(
                () -> assertThat(strategy.support(mock(ApplicationProcessedEvent.class))).isTrue(),
                () -> assertThat(strategy.support(mock(ClubJoinApplicationCreatedEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(OfficerRejectClubJoinApplicationEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(OfficerApproveClubJoinApplicationEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(NotificationEvent.class))).isFalse()
        );
    }

    @Test
    @DisplayName("ApplicationProcessedEvent 이벤트를 받아 알림을 생성한다.")
    void success_test_2() {
        // given
        Long receiverId = 1L;

        // when
        List<Notification> make = strategy.make(ApplicationProcessedEvent.reject(this, receiverId, 10L, 11L));

        // then
        assertThat(make.size()).isEqualTo(1);
    }
}