package com.mohaeng.applicationform.domain.event;


import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.event.BaseEventHistory;

public class RequestJoinClubEvent extends BaseEvent {

    private Long applicantId;  // 가입 신청자 ID
    private Long targetClubId;  // 모임 ID
    private Long applicationFormId;  // 가입 신청서 ID

    public RequestJoinClubEvent(final Object source,
                                final Long applicantId,
                                final Long targetClubId,
                                final Long applicationFormId) {
        super(source);
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
}
