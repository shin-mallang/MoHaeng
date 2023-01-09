package com.mohaeng.alarm.application.service;

import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.common.alarm.AlarmEvent;

public interface AlarmMessageGenerator {

    AlarmMessage generate(final AlarmEvent event);

    AlarmType alarmType();
}
