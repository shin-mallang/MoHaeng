package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

/**
 * 회장이 아닌 임원이 모임 가입 신청을 승인하였을 때 발행
 */
public class OfficerApproveClubJoinApplicationEvent extends AlarmEvent {

    private final Long managerMemberId;  // 관리자 ID (Member Id)
    private final Long managerParticipantId;  // 관리자 ID (Participant Id)
    private final Long applicantMemberId;  // 가입된 회원 ID (Member Id)
    private final Long applicantParticipantId;  // 가입된 회원 ID (Participant Id)
    private final Long applicationFormId;  // 처리된 가입 신청서 ID

    /**
     * @param receiverId             모임의 회장의 Member ID
     * @param managerMemberId        관리자 ID (Member Id)
     * @param managerParticipantId   관리자 ID (Participant Id)
     * @param applicantMemberId      가입된 회원 ID (Member Id)
     * @param applicantParticipantId 가입된 회원 ID (Participant Id)
     * @param applicationFormId      처리된 가입 신청서 ID
     */
    public OfficerApproveClubJoinApplicationEvent(final Object source,
                                                  final Long receiverId,
                                                  final Long managerMemberId,
                                                  final Long managerParticipantId,
                                                  final Long applicantMemberId,
                                                  final Long applicantParticipantId,
                                                  final Long applicationFormId) {
        super(source, List.of(receiverId));
        this.managerMemberId = managerMemberId;
        this.managerParticipantId = managerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicantParticipantId = applicantParticipantId;
        this.applicationFormId = applicationFormId;
    }

    @Override
    public BaseEventHistory history() {
        return new OfficerApproveClubJoinApplicationEventHistory(managerMemberId, managerParticipantId, applicantMemberId, applicantParticipantId, applicationFormId);
    }

    @Override
    public String toString() {
        return "OfficerApproveClubJoinApplicationEvent{" +
                "managerMemberId=" + managerMemberId +
                ", managerParticipantId=" + managerParticipantId +
                ", applicantMemberId=" + applicantMemberId +
                ", applicantParticipantId=" + applicantParticipantId +
                ", applicationFormId=" + applicationFormId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}


