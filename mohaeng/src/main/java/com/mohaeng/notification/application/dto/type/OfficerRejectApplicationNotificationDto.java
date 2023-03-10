package com.mohaeng.notification.application.dto.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.type.OfficerRejectApplicationNotificationResponse;

import java.time.LocalDateTime;

public class OfficerRejectApplicationNotificationDto extends NotificationDto {

    private final Long officerMemberId;  // 처리한 임원진 Member ID
    private final Long officerParticipantId;  // 처리한 임원진 Participant ID (Member랑 Participant랑 뭘 줘야할 지 몰라서 둘 다 줌)
    private final Long applicantMemberId;  // 거절된 회원의 Member ID

    public OfficerRejectApplicationNotificationDto(final Long id,
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

    public Long officerMemberId() {
        return officerMemberId;
    }

    public Long officerParticipantId() {
        return officerParticipantId;
    }

    public Long applicantMemberId() {
        return applicantMemberId;
    }

    @Override
    public NotificationResponse toResponse() {
        return new OfficerRejectApplicationNotificationResponse(id(), createdAt(), isRead(), type(), officerMemberId(), officerParticipantId(), applicantMemberId());
    }
}