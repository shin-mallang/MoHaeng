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

@DisplayName("ClubJoinApplicationCreatedNotificationMakeStrategy 는 ")
class ClubJoinApplicationCreatedNotificationMakeStrategyTest {

    private ClubJoinApplicationCreatedNotificationMakeStrategy strategy = new ClubJoinApplicationCreatedNotificationMakeStrategy();

    @Test
    @DisplayName("ClubJoinApplicationCreatedEvent 이벤트만을 처리할 수 있다.")
    void success_test_1() {
        // then
        Assertions.assertAll(
                () -> assertThat(strategy.support(mock(ClubJoinApplicationCreatedEvent.class))).isTrue(),
                () -> assertThat(strategy.support(mock(ApplicationProcessedEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(OfficerRejectClubJoinApplicationEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(OfficerApproveClubJoinApplicationEvent.class))).isFalse(),
                () -> assertThat(strategy.support(mock(NotificationEvent.class))).isFalse()
        );
    }

    @Test
    @DisplayName("ClubJoinApplicationCreatedEvent 이벤트를 받아 알림을 생성한다.")
    void success_test_2() {
        // given
        List<Long> memberIds = List.of(1L, 2L, 3L, 4L);

        // when
        List<Notification> make = strategy.make(new ClubJoinApplicationCreatedEvent(this, memberIds, 10L, 11L, 12L));

        // then
        assertThat(make.size()).isEqualTo(memberIds.size());
    }
}