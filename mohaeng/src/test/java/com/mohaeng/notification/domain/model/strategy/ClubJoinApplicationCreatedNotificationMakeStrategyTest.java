package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.participant.domain.event.ApplicationFormProcessedEvent;
import com.mohaeng.participant.domain.event.ApplicationFormWrittenEvent;
import com.mohaeng.participant.domain.event.OfficerApproveApplicationFormEvent;
import com.mohaeng.participant.domain.event.OfficerRejectApplicationFormEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClubJoinApplicationCreatedNotificationMakeStrategy 는 ")
class ClubJoinApplicationCreatedNotificationMakeStrategyTest {

    private ClubJoinApplicationCreatedNotificationMakeStrategy strategy = new ClubJoinApplicationCreatedNotificationMakeStrategy();

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("ClubJoinApplicationCreatedEvent 이벤트만을 처리할 수 있다.")
        void success_test_1() {
            // then
            Assertions.assertAll(
                    () -> assertThat(strategy.supportEvent()).isEqualTo(ApplicationFormWrittenEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormProcessedEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerApproveApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerRejectApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(NotificationEvent.class)
            );
        }

        @Test
        @DisplayName("ClubJoinApplicationCreatedEvent 이벤트를 받아 알림을 생성한다.")
        void success_test_2() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L, 4L);

            // when
            List<Notification> make = strategy.make(new ApplicationFormWrittenEvent(this, memberIds, 10L, 11L, 12L));

            // then
            assertThat(make.size()).isEqualTo(memberIds.size());
        }
    }
}