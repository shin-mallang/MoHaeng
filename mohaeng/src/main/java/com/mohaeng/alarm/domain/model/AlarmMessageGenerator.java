package com.mohaeng.alarm.domain.model;

import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.common.alarm.AlarmEvent;

public interface AlarmMessageGenerator {

    AlarmMessage generate(final AlarmEvent alarmEvent);

    AlarmType alarmType();
}