package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.participant.domain.event.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExpelParticipantNotificationMakeStrategy 는 ")
class ExpelParticipantNotificationMakeStrategyTest {

    private ExpelParticipantNotificationMakeStrategy strategy = new ExpelParticipantNotificationMakeStrategy();

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("ExpelParticipantEvent 이벤트만을 처리할 수 있다.")
        void success_test_1() {
            // then
            Assertions.assertAll(
                    () -> assertThat(strategy.supportEvent()).isEqualTo(ExpelParticipantEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerApproveApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormProcessedEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormWrittenEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerRejectApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(NotificationEvent.class)
            );
        }

        @Test
        @DisplayName("ExpelParticipantEvent 이벤트를 받아 알림을 생성한다.")
        void success_test_2() {
            // given
            Long receiverId = 1L;

            // when
            List<Notification> make = strategy.make(
                    new ExpelParticipantEvent(
                            this,
                            1L,
                            1L));

            // then
            assertThat(make.size()).isEqualTo(1);
        }
    }
}