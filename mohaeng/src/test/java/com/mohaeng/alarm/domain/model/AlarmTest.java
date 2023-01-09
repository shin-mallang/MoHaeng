package com.mohaeng.alarm.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Alarm 은 ")
class AlarmTest {

    @Test
    @DisplayName("읽을 수 있다.")
    void test() {
        // given
        Alarm alarm = new Alarm();

        // when
        boolean before = alarm.isRead();
        alarm.read();

        // then
        assertAll(
                () -> assertThat(before).isFalse(),
                () -> assertThat(alarm.isRead()).isTrue()
        );
    }
}