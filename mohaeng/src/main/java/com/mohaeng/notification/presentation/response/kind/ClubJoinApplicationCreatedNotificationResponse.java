package com.mohaeng.notification.presentation.response.kind;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public class ClubJoinApplicationCreatedNotificationResponse extends NotificationResponse {

    private Long clubId;  // 가입을 요청한 모임 ID
    private Long applicantId;  // 가입 신청자의 Member ID
    private Long applicationFormId;  // 가입 신청서 ID

    public ClubJoinApplicationCreatedNotificationResponse(final Long id,
                                                          final LocalDateTime createdAt,
                                                          final boolean isRead,
                                                          final String type,
                                                          final Long clubId,
                                                          final Long applicantId,
                                                          final Long applicationFormId) {
        super(id, createdAt, isRead, type);
        this.clubId = clubId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    public Long getClubId() {
        return clubId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public Long getApplicationFormId() {
        return applicationFormId;
    }
}
