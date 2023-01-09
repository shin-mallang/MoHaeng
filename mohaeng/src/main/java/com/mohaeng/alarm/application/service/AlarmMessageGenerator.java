package com.mohaeng.alarm.application.service;

import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.common.event.BaseEvent;

public interface AlarmMessageGenerator<T extends BaseEvent> {

    AlarmMessage generate(final T event);

    AlarmType alarmType();
}
