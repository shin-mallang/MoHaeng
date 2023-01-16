package com.mohaeng.alarm.domain.model.value;

import com.mohaeng.applicationform.domain.event.ApproveJoinClubEvent;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.common.event.BaseEvent;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableMap;

public enum AlarmType {

    REQUEST_CLUB_JOIN(RequestJoinClubEvent.class),  // 모암 가입 신청 이벤트에 대한 알림
    APPROVE_JOIN_CLUB(ApproveJoinClubEvent.class),  // 모임 가입 신청 수락 이벤트에 대한 알림
    OFFICER_APPROVE_CLUB_JOIN_APPLICATION(OfficerApproveClubJoinApplicationEvent.class),  // 임원진이 모임 가입 신청을 수락한 것에 대한 알림
    ;

    private static final Map<Class<? extends BaseEvent>, AlarmType> EVENT_CLASS_MAPPING =
            stream(values()).collect(toUnmodifiableMap(AlarmType::mappedEventClass, it -> it));

    private final Class<? extends BaseEvent> mappedEventClass;

    AlarmType(Class<? extends BaseEvent> mappedEventClass) {
        this.mappedEventClass = mappedEventClass;
    }

    public static AlarmType ofEvent(Class<? extends BaseEvent> event) {
        return ofNullable(EVENT_CLASS_MAPPING.get(event))
                .orElseThrow(() -> new IllegalArgumentException("해당 이벤트에 대응되는 알람 타입이 없습니다."));
    }

    public Class<? extends BaseEvent> mappedEventClass() {
        return mappedEventClass;
    }
}
