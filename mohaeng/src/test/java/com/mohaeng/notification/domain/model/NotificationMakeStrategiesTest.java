package com.mohaeng.notification.domain.model;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.participant.domain.event.ClubJoinApplicationCreatedEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.mock;

@ApplicationTest
@DisplayName("NotificationMakeStrategies 는 ")
class NotificationMakeStrategiesTest {

    @Autowired
    private NotificationMakeStrategies notificationMakeStrategies;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("이벤트에 대응되는 알림 생성 전략이 있다면 알림을 생성한다.")
        void success_test_1() {
            // given
            List<Long> memberIds = List.of(1L, 2L, 3L, 4L);

            // when
            List<Notification> make =
                    notificationMakeStrategies.make(new ClubJoinApplicationCreatedEvent(this, memberIds, 10L, 11L, 12L));

            // then
            Assertions.assertThat(make.size()).isEqualTo(memberIds.size());
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("이벤트에 대응되는 알림 생성 전략이 없다면 오류를 발생시킨다..")
        void fail_test_1() {
            // when & then
            Assertions.assertThatThrownBy(() -> notificationMakeStrategies.make(mock(NotificationEvent.class)))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}