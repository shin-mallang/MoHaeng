package com.mohaeng.notification.application.mapper;

import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.applicationform.domain.event.ClubJoinApplicationCreatedEvent;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.common.notification.NotificationEvent;
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
import com.mohaeng.notification.domain.model.value.Receiver;

import java.util.List;

public class NotificationApplicationMapper {

    public static List<Notification> mapEventToNotification(final NotificationEvent event) {
        /* 가입 요청 생성 */
        if (event instanceof ClubJoinApplicationCreatedEvent e) {
            return e.receiverIds().stream()
                    .map(id -> (Notification) new ClubJoinApplicationCreatedNotification(Receiver.of(id), e.clubId(), e.applicantId(), e.applicationFormId()))
                    .toList();
        }

        /* 가입 요청 처리 */
        if (event instanceof ApplicationProcessedEvent e) {
            return List.of(new ApplicationProcessedNotification(Receiver.of(e.receiverId()), e.clubId(), e.isApproved()));
        }

        /* 임원의 가입 요청 수락 */
        if (event instanceof OfficerApproveClubJoinApplicationEvent e) {
            return List.of(new OfficerApproveApplicationNotification(Receiver.of(e.receiverId()), e.officerMemberId(), e.officerParticipantId(), e.applicantMemberId(), e.applicantParticipantId()));
        }

        /* 임원의 가입 요청 거절 */
        if (event instanceof OfficerRejectClubJoinApplicationEvent e) {
            return List.of(new OfficerRejectApplicationNotification(Receiver.of(e.receiverId()), e.officerMemberId(), e.officerParticipantId(), e.applicantMemberId()));
        }
        throw new IllegalArgumentException("매핑되는 알림이 없습니다.");

        /**
         switch (event) {
         case ClubJoinApplicationRequestedEvent e -> {
         return e.receiverIds().stream()
         .map(id -> (Notification) new ClubJoinApplicationRequestedNotification(new Receiver(id), e.clubId(), e.applicantId(), e.applicationFormId()))
         .toList();
         }

         case ApplicationProcessedEvent e -> {
         return List.of(new ApplicationProcessedNotification(new Receiver(e.receiverId()), e.clubId(), e.isApproved()));
         }

         case OfficerApproveClubJoinApplicationEvent e -> {
         return List.of(new OfficerApproveApplicationNotification(new Receiver(e.receiverId()), e.officerMemberId(), e.officerParticipantId(), e.applicantMemberId(), e.applicantParticipantId()));
         }

         case OfficerRejectClbJoinApplicationEvent e -> {
         return List.of(new OfficerRejectApplicationNotification(new Receiver(e.receiverId()), e.officerMemberId(), e.officerParticipantId(), e.applicantMemberId()));
         }

         default -> throw new IllegalArgumentException("매핑되는 알림이 없습니다.");
         }
         */
    }

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
