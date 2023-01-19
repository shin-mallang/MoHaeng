package com.mohaeng.notification.application.mapper;

import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.ApplicationProcessedNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.ClubJoinApplicationCreatedNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.OfficerApproveApplicationNotificationDto;
import com.mohaeng.notification.application.usecase.dto.kind.OfficerRejectApplicationNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.kind.ClubJoinApplicationCreatedNotification;
import com.mohaeng.notification.domain.model.kind.OfficerApproveApplicationNotification;
import com.mohaeng.notification.domain.model.kind.OfficerRejectApplicationNotification;

public class NotificationApplicationMapper {

    public static NotificationDto toApplicationDto(final Notification notification) {
        /* 가입 요청 생성 */
        if (notification instanceof ClubJoinApplicationCreatedNotification n) {
            return new ClubJoinApplicationCreatedNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.clubId(), n.applicantId(), n.applicationFormId());
        }

        /* 가입 요청 처리 */
        if (notification instanceof ApplicationProcessedNotification n) {
            return new ApplicationProcessedNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.clubId(), n.isApproved());
        }

        /* 임원의 가입 요청 수락 */
        if (notification instanceof OfficerApproveApplicationNotification n) {
            return new OfficerApproveApplicationNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.officerMemberId(), n.officerParticipantId(), n.applicantMemberId(), n.applicantParticipantId());
        }

        /* 임원의 가입 요청 거절 */
        if (notification instanceof OfficerRejectApplicationNotification n) {
            return new OfficerRejectApplicationNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.officerMemberId(), n.officerParticipantId(), n.applicantMemberId());
        }
        throw new IllegalArgumentException("매핑되는 알림이 없습니다.");

        /**
         switch (notification) {
         case ClubJoinApplicationRequestedNotification n ->
         new ClubJoinApplicationRequestedNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.clubId(), n.applicantId(), n.applicationFormId());

         case ApplicationProcessedNotification n ->
         new ApplicationProcessedNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.clubId(), n.isApproved());

         case OfficerApproveApplicationNotification n ->
         new OfficerApproveApplicationNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.officerMemberId(), n.officerParticipantId(), n.applicantMemberId(), n.applicantParticipantId());

         case OfficerRejectApplicationNotification n ->
         new OfficerRejectApplicationNotificationDto(n.id(), n.createdAt(), n.isRead(), n.getClass().getSimpleName(), n.officerMemberId(), n.officerParticipantId(), n.applicantMemberId());

         default -> throw new IllegalArgumentException("매핑되는 알림이 없습니다.");
         };
         */
    }
}
