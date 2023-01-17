package com.mohaeng.alarm.domain.value;

import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.applicationform.domain.event.ApproveJoinClubEvent;
import com.mohaeng.common.event.BaseEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.alarm.domain.model.value.AlarmType.APPROVE_JOIN_CLUB;

@DisplayName("AlarmType 은 ")
class AlarmTypeTest {

    @Test
    @DisplayName("이벤트에 대응되는 알림이 있으면 해당 알림 타입을 반환한다.")
    void test() {
        // when
        AlarmType alarmType = AlarmType.ofEvent(ApproveJoinClubEvent.class);

        // then
        Assertions.assertThat(alarmType).isEqualTo(APPROVE_JOIN_CLUB);
    }

    @Test
    @DisplayName("이벤트에 대응되는 알림이 없으면 예외를 반환한다.")
    void test2() {
        // when & then
        Assertions.assertThatThrownBy(() -> AlarmType.ofEvent(BaseEvent.class)).isInstanceOf(IllegalArgumentException.class);
    }
}