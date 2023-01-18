package com.mohaeng.notification.presentation.mapper;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.ApplicationProcessedNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.ClubJoinApplicationRequestedNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.OfficerApproveApplicationNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.OfficerRejectApplicationNotificationDto;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import com.mohaeng.notification.presentation.response.kind.ApplicationProcessedNotificationResponse;
import com.mohaeng.notification.presentation.response.kind.ClubJoinApplicationRequestedNotificationResponse;
import com.mohaeng.notification.presentation.response.kind.OfficerApproveApplicationNotificationResponse;
import com.mohaeng.notification.presentation.response.kind.OfficerRejectApplicationNotificationResponse;

public class NotificationControllerMapper {

    public static NotificationResponse toResponseDto(final NotificationDto notificationDto) {
        /* 가입 요청 생성 */
        if (notificationDto instanceof ClubJoinApplicationRequestedNotificationDto dto) {
            return new ClubJoinApplicationRequestedNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.clubId(), dto.applicantId(), dto.applicationFormId());
        }

        /* 가입 요청 처리 */
        if (notificationDto instanceof ApplicationProcessedNotificationDto dto) {
            return new ApplicationProcessedNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.clubId(), dto.isApproved());
        }

        /* 임원의 가입 요청 수락 */
        if (notificationDto instanceof OfficerApproveApplicationNotificationDto dto) {
            return new OfficerApproveApplicationNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.officerMemberId(), dto.officerParticipantId(), dto.applicantMemberId(), dto.applicantParticipantId());
        }

        /* 임원의 가입 요청 거절 */
        if (notificationDto instanceof OfficerRejectApplicationNotificationDto dto) {
            return new OfficerRejectApplicationNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.officerMemberId(), dto.officerParticipantId(), dto.applicantMemberId());
        }
        throw new IllegalArgumentException("매핑되는 알림이 없습니다.");

        /**
         return switch (notificationDto) {
         case ClubJoinApplicationRequestedNotificationDto dto ->
         new ClubJoinApplicationRequestedNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.clubId(), dto.applicantId(), dto.applicationFormId());

         case ApplicationProcessedNotificationDto dto ->
         new ApplicationProcessedNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.clubId(), dto.isApproved());

         case OfficerApproveApplicationNotificationDto dto ->
         new OfficerApproveApplicationNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.officerMemberId(), dto.officerParticipantId(), dto.applicantMemberId(), dto.applicantParticipantId());

         case OfficerRejectApplicationNotificationDto dto ->
         new OfficerRejectApplicationNotificationResponse(dto.id(), dto.createdAt(), dto.isRead(), dto.type(), dto.officerMemberId(), dto.officerParticipantId(), dto.applicantMemberId());

         default -> throw new IllegalArgumentException("매핑되는 알림이 없습니다.");
         };
         */
    }
}
