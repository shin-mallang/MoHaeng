package com.mohaeng.alarm.application.service;

import com.mohaeng.alarm.domain.model.value.AlarmType;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class AlarmMessageGenerateFactory {

    private Map<AlarmType, AlarmMessageGenerator> generators;

    public AlarmMessageGenerateFactory(final Set<AlarmMessageGenerator> generators) {
        this.generators = generators.stream()
                .collect(toUnmodifiableMap(AlarmMessageGenerator::alarmType, strategy -> strategy));
    }

    public AlarmMessageGenerator getGenerator(final AlarmType alarmType) {
        if (!generators.containsKey(alarmType)) {
            throw new IllegalArgumentException("알람 타입에 대응되는 메세지 생성기가 없습니다.");
        }
        return generators.get(alarmType);
    }
}
