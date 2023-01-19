package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.OfficerApproveApplicationNotificationResponse;

import java.time.LocalDateTime;

public class OfficerApproveApplicationNotificationDto extends NotificationDto {

    private Long officerMemberId;  // 처리한 임원진 Member ID
    private Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)
    private Long applicantMemberId;  // 가입된 회원의 Member ID
    private Long applicantParticipantId;  // 가입된 회원의 Participant ID

    public OfficerApproveApplicationNotificationDto(final Long id,
                                                    final LocalDateTime createdAt,
                                                    final boolean isRead,
                                                    final String type,
                                                    final Long officerMemberId,
                                                    final Long officerParticipantId,
                                                    final Long applicantMemberId,
                                                    final Long applicantParticipantId) {
        super(id, createdAt, isRead, type);
        this.officerMemberId = officerMemberId;
        this.officerParticipantId = officerParticipantId;
        this.applicantMemberId = applicantMemberId;
        this.applicantParticipantId = applicantParticipantId;
    }

    public Long officerMemberId() {
        return officerMemberId;
    }

    public Long officerParticipantId() {
        return officerParticipantId;
    }

    public Long applicantMemberId() {
        return applicantMemberId;
    }

    public Long applicantParticipantId() {
        return applicantParticipantId;
    }

    @Override
    public NotificationResponse toResponse() {
        return new OfficerApproveApplicationNotificationResponse(id(), createdAt(), isRead(), type(), officerMemberId(), officerParticipantId(), applicantMemberId(), applicantParticipantId());
    }
}
