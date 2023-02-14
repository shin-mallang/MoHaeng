package com.mohaeng.club.applicationform.domain.event;

import com.mohaeng.club.applicationform.domain.event.history.FillOutApplicationFormEventHistory;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

public class FillOutApplicationFormEvent extends NotificationEvent {

    private final Long clubId;  // 가입을 요청한 모임 ID
    private final Long applicantId;  // 가입 신청자의 Member ID
    private final Long applicationFormId;  // 가입 신청서 ID

    public FillOutApplicationFormEvent(final Object source,
                                       final List<Long> receiverIds,
                                       final Long clubId,
                                       final Long applicantId,
                                       final Long applicationFormId) {
        super(source, receiverIds);
        this.clubId = clubId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    @Override
    public BaseEventHistory history() {
        return new FillOutApplicationFormEventHistory(eventDateTime, clubId, applicantId, applicationFormId);
    }

    public Long clubId() {
        return clubId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    @Override
    public String toString() {
        return "FillOutApplicationFormEvent{" +
                "clubId=" + clubId +
                ", applicantId=" + applicantId +
                ", applicationFormId=" + applicationFormId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}