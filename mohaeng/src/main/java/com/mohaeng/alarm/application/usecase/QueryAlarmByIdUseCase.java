package com.mohaeng.alarm.application.usecase;

import com.mohaeng.alarm.application.usecase.dto.AlarmDto;

public interface QueryAlarmByIdUseCase {

    AlarmDto query(final Query query);

    record Query(
            Long alarmId,
            Long memberId
    ) {
    }
}
