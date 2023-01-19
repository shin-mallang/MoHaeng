package com.mohaeng.notification.presentation.response.kind;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class OfficerRejectApplicationNotificationResponse extends NotificationResponse {

    private Long officerMemberId;  // 처리한 임원진 Member ID
    private Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)
    private Long applicantMemberId;  // 거절된 회원의 Member ID

    public OfficerRejectApplicationNotificationResponse(final Long id,
                                                        final LocalDateTime createdAt,
                                                        final boolean isRead,
                                                        final String type,
                                                        final Long officerMemberId,
                                                        final Long officerParticipantId,
                                                        final Long applicantMemberId) {
        super(id, createdAt, isRead, type);
        this.officerMemberId = officerMemberId;
        this.officerParticipantId = officerParticipantId;
        this.applicantMemberId = applicantMemberId;
    }

    public Long getOfficerMemberId() {
        return officerMemberId;
    }

    public Long getOfficerParticipantId() {
        return officerParticipantId;
    }

    public Long getApplicantMemberId() {
        return applicantMemberId;
    }
}
