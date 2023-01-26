package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

/**
 * 회장이 아닌 임원이 모임 가입 신청을 승인하였을 때 발행
 */
public class OfficerApproveClubJoinApplicationEvent extends NotificationEvent {

    private final Long officerMemberId;  // 관리자 ID (Member Id)
    private final Long officerParticipantId;  // 관리자 ID (Participant Id)
    private final Long applicantMemberId;  // 가입된 회원 ID (Member Id)
    private final Long applicantParticipantId;  // 가입된 회원 ID (Participant Id)
    private final Long applicationFormId;  // 처리된 가입 신청서 ID

    /**
     * @param receiverId             모임의 회장의 Member ID
     * @param officerMemberId        관리자 ID (Member Id)
     * @param officerParticipantId   관리자 ID (Participant Id)
     * @param applicantMemberId      가입된 회원 ID (Member Id)
     * @param applicantParticipantId 가입된 회원 ID (Participant Id)
     * @param applicationFormId      처리된 가입 신청서 ID
     */
    public OfficerApproveClubJoinApplicationEvent(final Object source,
                                                  final Long receiverId,
                                                  final Long officerMemberId,
                                                  final Long officerParticipantId,
                                                  final Long applicantMemberId,
                                                  final Long applicantParticipantId,
                                                  final Long applicationFormId) {
        super(source, receiverId);
        this.officerMemberId = officerMemberId;
        this.officerParticipantId = officerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicantParticipantId = applicantParticipantId;
        this.applicationFormId = applicationFormId;
    }

    public Long receiverId() {
        return receiverIds.get(0);
    }

    public Long applicantMemberId() {
        return applicantMemberId;
    }

    public Long applicantParticipantId() {
        return applicantParticipantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public Long officerMemberId() {
        return officerMemberId;
    }

    public Long officerParticipantId() {
        return officerParticipantId;
    }

    @Override
    public BaseEventHistory history() {
        return new OfficerApproveClubJoinApplicationEventHistory(officerMemberId, officerParticipantId, applicantMemberId, applicantParticipantId, applicationFormId);
    }

    @Override
    public String toString() {
        return "OfficerApproveClubJoinApplicationEvent{" +
                "managerMemberId=" + officerMemberId +
                ", managerParticipantId=" + officerParticipantId +
                ", applicantMemberId=" + applicantMemberId +
                ", applicantParticipantId=" + applicantParticipantId +
                ", applicationFormId=" + applicationFormId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}


