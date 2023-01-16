package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class RequestJoinClubEvent extends AlarmEvent {

    private final Long applicantId;  // 가입 신청자 ID
    private final Long targetClubId;  // 모임 ID
    private final Long applicationFormId;  // 가입 신청서 ID

    public RequestJoinClubEvent(final Object source,
                                final Long applicantId,
                                final Long targetClubId,
                                final Long applicationFormId,
                                final List<Long> receiverIds) {
        super(source, receiverIds);
        this.applicantId = applicantId;
        this.targetClubId = targetClubId;
        this.applicationFormId = applicationFormId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long targetClubId() {
        return targetClubId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    @Override
    public BaseEventHistory history() {
        return new RequestJoinClubEventHistory(eventDateTime, applicantId, targetClubId, applicationFormId);
    }

    @Override
    public String toString() {
        return "RequestJoinClubEvent{" +
                "applicantId=" + applicantId +
                ", targetClubId=" + targetClubId +
                ", applicationFormId=" + applicationFormId +
                ", receiverIds=" + receiverIds.stream()
                .map(String::valueOf)
                .collect(joining(",")) +
                '}';
    }
}
