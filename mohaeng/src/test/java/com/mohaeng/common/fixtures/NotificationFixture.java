package com.mohaeng.common.fixtures;

import com.mohaeng.notification.application.dto.kind.ApplicationProcessedNotificationDto;
import com.mohaeng.notification.application.dto.kind.ClubJoinApplicationCreatedNotificationDto;
import com.mohaeng.notification.application.dto.kind.OfficerApproveApplicationNotificationDto;
import com.mohaeng.notification.application.dto.kind.OfficerRejectApplicationNotificationDto;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.kind.ClubJoinApplicationCreatedNotification;
import com.mohaeng.notification.domain.model.kind.OfficerApproveApplicationNotification;
import com.mohaeng.notification.domain.model.kind.OfficerRejectApplicationNotification;

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
}
