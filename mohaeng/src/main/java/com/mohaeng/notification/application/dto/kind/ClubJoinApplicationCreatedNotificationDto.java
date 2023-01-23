package com.mohaeng.notification.application.dto.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.ClubJoinApplicationCreatedNotificationResponse;

import java.time.LocalDateTime;

public class ClubJoinApplicationCreatedNotificationDto extends NotificationDto {

    private final Long clubId;  // 가입을 요청한 모임 ID
    private final Long applicantId;  // 가입 신청자의 Member ID
    private final Long applicationFormId;  // 가입 신청서 ID

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

    @Override
    public NotificationResponse toResponse() {
        return new ClubJoinApplicationCreatedNotificationResponse(id(), createdAt(), isRead(), type(), clubId(), applicantId(), applicationFormId());
    }
}
