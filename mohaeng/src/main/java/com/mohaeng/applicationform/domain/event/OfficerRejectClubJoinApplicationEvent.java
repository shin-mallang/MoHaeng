package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

/**
 * 회장이 아닌 임원이 모임 가입 신청을 거절하였을 때 발행
 */
public class OfficerRejectClubJoinApplicationEvent extends AlarmEvent {

    private final Long managerMemberId;  // 관리자 ID (Member Id)
    private final Long managerParticipantId;  // 관리자 ID (Participant Id)
    private final Long applicantMemberId;  // 거절된 회원 ID (Member Id)
    private final Long applicationFormId;  // 처리된 가입 신청서 ID

    /**
     * @param receiverId           모임의 회장의 Member ID
     * @param managerMemberId      관리자 ID (Member Id)
     * @param managerParticipantId 관리자 ID (Participant Id)
     * @param applicantMemberId    거절된 회원 ID (Member Id)
     * @param applicationFormId    처리된 가입 신청서 ID
     */
    public OfficerRejectClubJoinApplicationEvent(final Object source,
                                                 final Long receiverId,
                                                 final Long managerMemberId,
                                                 final Long managerParticipantId,
                                                 final Long applicantMemberId,
                                                 final Long applicationFormId) {
        super(source, List.of(receiverId));
        this.managerMemberId = managerMemberId;
        this.managerParticipantId = managerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicationFormId = applicationFormId;
    }

    @Override
    public BaseEventHistory history() {
        return new OfficerRejectClubJoinApplicationEventHistory(managerMemberId, managerParticipantId, applicantMemberId, applicationFormId);
    }

    @Override
    public String toString() {
        return "OfficerApproveClubJoinApplicationEvent{" +
                "managerMemberId=" + managerMemberId +
                ", managerParticipantId=" + managerParticipantId +
                ", applicantMemberId=" + applicantMemberId +
                ", applicationFormId=" + applicationFormId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}


