package com.mohaeng.common.fixtures;

import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.alarm.domain.model.value.Receiver;

public class AlarmFixture {

    private static final Receiver RECEIVER = Receiver.of(MemberFixture.member(null));
    private static final AlarmMessage ALARM_MESSAGE = new AlarmMessage("알람 제목", "알람 메세지");

    public static Alarm alarm() {
        return Alarm.of(RECEIVER, ALARM_MESSAGE, AlarmType.REQUEST_CLUB_JOIN);
    }
}
