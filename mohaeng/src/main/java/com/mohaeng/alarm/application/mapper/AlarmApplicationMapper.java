package com.mohaeng.alarm.application.mapper;

import com.mohaeng.alarm.application.usecase.dto.AlarmDto;
import com.mohaeng.alarm.domain.model.Alarm;

public class AlarmApplicationMapper {

    public static AlarmDto toPresentationDto(final Alarm alarm) {
        return new AlarmDto(
                alarm.createdAt(),
                alarm.alarmMessage().title(),
                alarm.alarmMessage().content(),
                alarm.alarmType().name(),
                alarm.isRead()
        );
    }
}
