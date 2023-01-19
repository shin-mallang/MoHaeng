package com.mohaeng.notification.application.usecase.dto.kind;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;

import java.time.LocalDateTime;

public class ClubJoinApplicationCreatedNotificationDto extends NotificationDto {

    private Long clubId;  // 가입을 요청한 모임 ID
    private Long applicantId;  // 가입 신청자의 Member ID
    private Long applicationFormId;  // 가입 신청서 ID

    public ClubJoinApplicationCreatedNotificationDto(final Long id,
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

    public Long clubId() {
        return clubId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }
}
