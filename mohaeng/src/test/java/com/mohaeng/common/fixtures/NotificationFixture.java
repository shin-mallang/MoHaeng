package com.mohaeng.common.fixtures;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_DESCRIPTION;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_NAME;

public class NotificationFixture {

    public static final Long RECEIVER_ID = 1L;

    public static FillOutApplicationFormNotification fillOutApplicationFormNotification(final Long id) {
        FillOutApplicationFormNotification notification = new FillOutApplicationFormNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static ApplicationProcessedNotification applicationProcessedNotification(final Long id) {
        ApplicationProcessedNotification notification = new ApplicationProcessedNotification(Receiver.of(RECEIVER_ID), 2L, true);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static OfficerApproveApplicationNotification officerApproveApplicationNotification(final Long id) {
        OfficerApproveApplicationNotification notification = new OfficerApproveApplicationNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L, 5L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static OfficerRejectApplicationNotification officerRejectApplicationNotification(final Long id) {
        OfficerRejectApplicationNotification notification = new OfficerRejectApplicationNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static DeleteApplicationFormCauseByClubDeletedNotification deleteApplicationFormCauseByClubDeletedNotification(final Long id) {
        DeleteApplicationFormCauseByClubDeletedNotification notification = new DeleteApplicationFormCauseByClubDeletedNotification(Receiver.of(RECEIVER_ID), ANA_NAME, ANA_DESCRIPTION);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static DeleteParticipantCauseByClubDeletedNotification deleteParticipantCauseByClubDeletedNotification(final Long id) {
        DeleteParticipantCauseByClubDeletedNotification notification = new DeleteParticipantCauseByClubDeletedNotification(Receiver.of(RECEIVER_ID), ANA_NAME, ANA_DESCRIPTION);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static ExpelParticipantNotification expelParticipantNotification(final Long id) {
        ExpelParticipantNotification notification = new ExpelParticipantNotification(Receiver.of(RECEIVER_ID), 2L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static ParticipantClubRoleChangedNotification participantClubRoleChangedNotification(final Long id) {
        ParticipantClubRoleChangedNotification notification = new ParticipantClubRoleChangedNotification(Receiver.of(RECEIVER_ID), 2L, 3L, "변경된 역할이름", OFFICER);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    public static List<Notification> allKindNotifications() {
        return List.of(
                officerApproveApplicationNotification(null),
                applicationProcessedNotification(null),
                deleteApplicationFormCauseByClubDeletedNotification(null),
                deleteParticipantCauseByClubDeletedNotification(null),
                expelParticipantNotification(null),
                fillOutApplicationFormNotification(null),
                officerRejectApplicationNotification(null),
                participantClubRoleChangedNotification(null)
        );
    }

    public static List<Notification> allKindNotificationsWithReceiverId(final Long receiverId) {
        return Stream.of(
                officerApproveApplicationNotification(null),
                applicationProcessedNotification(null),
                deleteApplicationFormCauseByClubDeletedNotification(null),
                deleteParticipantCauseByClubDeletedNotification(null),
                expelParticipantNotification(null),
                fillOutApplicationFormNotification(null),
                officerRejectApplicationNotification(null),
                participantClubRoleChangedNotification(null)
        ).peek(it -> ReflectionTestUtils.setField(it, "receiver", Receiver.of(receiverId))).toList();
    }
}
