package com.mohaeng.alarm.domain.model.value;

import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.common.event.BaseEvent;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableMap;

public enum AlarmType {

    // 클럽 가입 신청 이벤트에 대한 알람
    REQUEST_CLUB_JOIN(RequestJoinClubEvent.class),
    ;

    private static final Map<Class<? extends BaseEvent>, AlarmType> EVENT_CLASS_MAPPING =
            stream(values()).collect(toUnmodifiableMap(AlarmType::mappedEventClass, it -> it));

    private final Class<? extends BaseEvent> mappedEventClass;

    AlarmType(Class<? extends BaseEvent> mappedEventClass) {
        this.mappedEventClass = mappedEventClass;
    }

    public Class<? extends BaseEvent> mappedEventClass() {
        return mappedEventClass;
    }

    public static AlarmType ofEvent(Class<? extends BaseEvent> event) {
        return ofNullable(EVENT_CLASS_MAPPING.get(event))
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트에 대응되는 알람 타입이 없습니다."));
    }
}
