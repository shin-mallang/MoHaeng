package com.mohaeng.common.fixtures;

import com.mohaeng.notification.application.dto.kind.*;
import com.mohaeng.notification.domain.model.kind.*;

import java.time.LocalDateTime;

public class NotificationFixture {

    public static ApplicationProcessedNotificationDto applicationProcessedNotificationDto(final Long id) {
        return new ApplicationProcessedNotificationDto(id,
                LocalDateTime.now(),
                true,
                ApplicationProcessedNotification.class.getSimpleName(),
                1L,
                true
        );
    }

    public static ClubJoinApplicationCreatedNotificationDto clubJoinApplicationCreatedNotificationDto(final Long id) {
        return new ClubJoinApplicationCreatedNotificationDto(id,
                LocalDateTime.now(),
                true,
                ClubJoinApplicationCreatedNotification.class.getSimpleName(),
                1L,
                2L,
                3L
        );
    }

    public static OfficerApproveApplicationNotificationDto officerApproveApplicationNotificationDto(final Long id) {
        return new OfficerApproveApplicationNotificationDto(id,
                LocalDateTime.now(),
                true,
                OfficerApproveApplicationNotification.class.getSimpleName(),
                1L,
                2L,
                3L,
                4L
        );
    }

    public static OfficerRejectApplicationNotificationDto officerRejectApplicationNotificationDto(final Long id) {
        return new OfficerRejectApplicationNotificationDto(id,
                LocalDateTime.now(),
                true,
                OfficerRejectApplicationNotification.class.getSimpleName(),
                1L,
                2L,
                3L
        );
    }

    public static ExpelParticipantNotificationDto expelledParticipantNotificationDto(final Long id) {
        return new ExpelParticipantNotificationDto(id,
                LocalDateTime.now(),
                true,
                ExpelParticipantNotification.class.getSimpleName(),
                1L);
    }

    public static DeleteApplicationFormBecauseClubIsDeletedNotificationDto deleteApplicationFormBecauseClubIsDeletedNotificationDto(final Long id) {
        return new DeleteApplicationFormBecauseClubIsDeletedNotificationDto(id,
                LocalDateTime.now(),
                true,
                DeleteApplicationFormBecauseClubIsDeletedNotification.class.getSimpleName(),
                "ANA",
                "알고리즘 동아리입니다.");
    }

    public static DeleteParticipantBecauseClubIsDeletedNotificationDto deleteParticipantBecauseClubIsDeletedNotificationDto(final Long id) {
        return new DeleteParticipantBecauseClubIsDeletedNotificationDto(id,
                LocalDateTime.now(),
                true,
                DeleteParticipantBecauseClubIsDeletedNotification.class.getSimpleName(),
                "ANA",
                "알고리즘 동아리입니다.");
    }
}
