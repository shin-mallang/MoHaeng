package com.mohaeng.alarm.presentation.mapper;

import com.mohaeng.alarm.application.usecase.dto.AlarmDto;
import com.mohaeng.alarm.presentation.QueryAlarmByIdController;

public class AlarmControllerMapper {

    public static QueryAlarmByIdController.AlarmResponse toResponseDto(final AlarmDto alarmDto) {
        return new QueryAlarmByIdController.AlarmResponse(
                alarmDto.createdAt(),
                alarmDto.title(),
                alarmDto.content(),
                alarmDto.alarmType(),
                alarmDto.isRead()
        );
    }
}
