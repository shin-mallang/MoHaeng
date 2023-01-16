package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

public class ApproveJoinClubEvent extends AlarmEvent {

    private final Long managerId;  // 관리자 ID (Participant Id)
    private final Long applicantId;  // 가입된 회원 ID (ParticipantId)
    private final Long applicationFormId;  // 처리된 가입 신청서 ID

    /**
     * @param receiverId 모임의 회장의 Member ID
     */
    public ApproveJoinClubEvent(final Object source,
                                final Long receiverId,
                                final Long managerId,
                                final Long applicantId,
                                final Long applicationFormId) {
        super(source, List.of(receiverId));
        this.managerId = managerId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    @Override
    public String toString() {
        return "ApproveJoinClubEvent{" +
                "managerId=" + managerId +
                ", applicantId=" + applicantId +
                ", applicationFormId=" + applicationFormId +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new ApproveJoinClubEventHistory(eventDateTime, managerId, applicantId, applicationFormId);
    }
}
