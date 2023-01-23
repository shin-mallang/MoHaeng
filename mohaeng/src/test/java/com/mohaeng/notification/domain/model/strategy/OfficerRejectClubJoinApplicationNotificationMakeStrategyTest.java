package com.mohaeng.notification.domain.model.strategy;

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

@DisplayName("OfficerRejectClubJoinApplicationNotificationMakeStrategy 는 ")
class OfficerRejectClubJoinApplicationNotificationMakeStrategyTest {

    private OfficerRejectClubJoinApplicationNotificationMakeStrategy strategy = new OfficerRejectClubJoinApplicationNotificationMakeStrategy();

    @Test
    @DisplayName("OfficerRejectClubJoinApplicationEvent 이벤트만을 처리할 수 있다.")
    void success_test_1() {
        // then
        Assertions.assertAll(
                () -> assertThat(strategy.supportEvent()).isEqualTo(OfficerRejectClubJoinApplicationEvent.class),
                () -> assertThat(strategy.supportEvent()).isNotEqualTo(ClubJoinApplicationCreatedEvent.class),
                () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationProcessedEvent.class),
                () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerApproveClubJoinApplicationEvent.class),
                () -> assertThat(strategy.supportEvent()).isNotEqualTo(NotificationEvent.class)
        );
    }

    @Test
    @DisplayName("OfficerRejectClubJoinApplicationEvent 이벤트를 받아 알림을 생성한다.")
    void success_test_2() {
        // given
        Long receiverId = 1L;

        // when
        List<Notification> make = strategy.make(
                new OfficerRejectClubJoinApplicationEvent(
                        this,
                        receiverId,
                        10L,
                        11L,
                        12L,
                        13L));

        // then
        assertThat(make.size()).isEqualTo(1);
    }
}