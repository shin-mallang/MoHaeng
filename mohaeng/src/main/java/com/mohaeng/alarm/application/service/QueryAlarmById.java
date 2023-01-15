package com.mohaeng.alarm.application.service;

import com.mohaeng.alarm.application.mapper.AlarmApplicationMapper;
import com.mohaeng.alarm.application.usecase.QueryAlarmByIdUseCase;
import com.mohaeng.alarm.application.usecase.dto.AlarmDto;
import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import com.mohaeng.alarm.exception.AlarmException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.alarm.exception.AlarmExceptionType.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional(readOnly = true)
public class QueryAlarmById implements QueryAlarmByIdUseCase {

    private final AlarmRepository alarmRepository;

    public QueryAlarmById(final AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    @Override
    public AlarmDto query(final Query query) {
        Alarm alarm = alarmRepository.findById(query.id()).orElseThrow(() -> new AlarmException(NOT_FOUND_APPLICATION_FORM));

        alarm.read();  // 알림 읽음 처리

        return AlarmApplicationMapper.toPresentationDto(alarm);
    }
}
