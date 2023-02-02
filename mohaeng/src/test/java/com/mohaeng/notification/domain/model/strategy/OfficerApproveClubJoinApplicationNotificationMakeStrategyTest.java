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

@DisplayName("OfficerApproveClubJoinApplicationNotificationMakeStrategy 는 ")
class OfficerApproveClubJoinApplicationNotificationMakeStrategyTest {

    private OfficerApproveClubJoinApplicationNotificationMakeStrategy strategy = new OfficerApproveClubJoinApplicationNotificationMakeStrategy();

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("OfficerApproveClubJoinApplicationEvent 이벤트만을 처리할 수 있다.")
        void success_test_1() {
            // then
            Assertions.assertAll(
                    () -> assertThat(strategy.supportEvent()).isEqualTo(OfficerApproveApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormProcessedEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormWrittenEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerRejectApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(NotificationEvent.class)
            );
        }

        @Test
        @DisplayName("OfficerApproveClubJoinApplicationEvent 이벤트를 받아 알림을 생성한다.")
        void success_test_2() {
            // given
            Long receiverId = 1L;

            // when
            List<Notification> make = strategy.make(
                    new OfficerApproveApplicationFormEvent(
                            this,
                            receiverId,
                            10L,
                            11L,
                            12L,
                            13L,
                            14L));

            // then
            assertThat(make.size()).isEqualTo(1);
        }
    }
}