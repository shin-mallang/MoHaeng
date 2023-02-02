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

@DisplayName("DeleteParticipantBecauseClubIsDeletedNotificationMakeStrategy 는 ")
class DeleteParticipantBecauseClubIsDeletedNotificationMakeStrategyTest {

    private DeleteParticipantBecauseClubIsDeletedNotificationMakeStrategy strategy = new DeleteParticipantBecauseClubIsDeletedNotificationMakeStrategy();

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("DeleteParticipantEvent 이벤트만을 처리할 수 있다.")
        void success_test_1() {
            // then
            Assertions.assertAll(
                    () -> assertThat(strategy.supportEvent()).isEqualTo(DeleteParticipantEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ExpelParticipantEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerApproveApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormProcessedEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(ApplicationFormWrittenEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(OfficerRejectApplicationFormEvent.class),
                    () -> assertThat(strategy.supportEvent()).isNotEqualTo(NotificationEvent.class)
            );
        }

        @Test
        @DisplayName("DeleteParticipantEvent 이벤트를 받아 알림을 생성한다.")
        void success_test_2() {
            // given
            List<Long> receiverIds = List.of(1L, 2L);

            // when
            List<Notification> make = strategy.make(
                    new DeleteParticipantEvent(
                            this,
                            receiverIds,
                            "name",
                            "des"));

            // then
            assertThat(make.size()).isEqualTo(receiverIds.size());
        }
    }
}