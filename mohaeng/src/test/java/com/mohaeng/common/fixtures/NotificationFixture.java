package com.mohaeng.common.fixtures;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_DESCRIPTION;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_NAME;

public class NotificationFixture {

    public static final Long RECEIVER_ID = 1L;

    public static FillOutApplicationFormNotification fillOutApplicationFormNotification(final Long id, final Long receiverId) {
        FillOutApplicationFormNotification notification = new FillOutApplicationFormNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static ApplicationProcessedNotification applicationProcessedNotification(final Long id, final Long receiverId) {
        ApplicationProcessedNotification notification = new ApplicationProcessedNotification(Receiver.of(RECEIVER_ID), 2L, true);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static OfficerApproveApplicationNotification officerApproveApplicationNotification(final Long id, final Long receiverId) {
        OfficerApproveApplicationNotification notification = new OfficerApproveApplicationNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L, 5L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static OfficerRejectApplicationNotification officerRejectApplicationNotification(final Long id, final Long receiverId) {
        OfficerRejectApplicationNotification notification = new OfficerRejectApplicationNotification(Receiver.of(RECEIVER_ID), 2L, 3L, 4L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static DeleteApplicationFormCauseByClubDeletedNotification deleteApplicationFormCauseByClubDeletedNotification(final Long id, final Long receiverId) {
        DeleteApplicationFormCauseByClubDeletedNotification notification = new DeleteApplicationFormCauseByClubDeletedNotification(Receiver.of(RECEIVER_ID), ANA_NAME, ANA_DESCRIPTION);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static DeleteParticipantCauseByClubDeletedNotification deleteParticipantCauseByClubDeletedNotification(final Long id, final Long receiverId) {
        DeleteParticipantCauseByClubDeletedNotification notification = new DeleteParticipantCauseByClubDeletedNotification(Receiver.of(RECEIVER_ID), ANA_NAME, ANA_DESCRIPTION);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static ExpelParticipantNotification expelParticipantNotification(final Long id, final Long receiverId) {
        ExpelParticipantNotification notification = new ExpelParticipantNotification(Receiver.of(RECEIVER_ID), 2L);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static ParticipantClubRoleChangedNotification participantClubRoleChangedNotification(final Long id, final Long receiverId) {
        ParticipantClubRoleChangedNotification notification = new ParticipantClubRoleChangedNotification(Receiver.of(RECEIVER_ID), 2L, 3L, "변경된 역할이름", OFFICER);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(notification, "receiver", Receiver.of(receiverId));
        return notification;
    }

    public static List<Notification> noIdAllKindNotifications(final Long receiverId) {
        return List.of(
                officerApproveApplicationNotification(null, receiverId),
                applicationProcessedNotification(null, receiverId),
                deleteApplicationFormCauseByClubDeletedNotification(null, receiverId),
                deleteParticipantCauseByClubDeletedNotification(null, receiverId),
                expelParticipantNotification(null, receiverId),
                fillOutApplicationFormNotification(null, receiverId),
                officerRejectApplicationNotification(null, receiverId),
                participantClubRoleChangedNotification(null, receiverId)
        );
    }

    public static List<Notification> allKindNotificationsWithId(long id, final Long receiverId) {
        return List.of(
                officerApproveApplicationNotification(id++, receiverId),
                applicationProcessedNotification(id++, receiverId),
                deleteApplicationFormCauseByClubDeletedNotification(id++, receiverId),
                deleteParticipantCauseByClubDeletedNotification(id++, receiverId),
                expelParticipantNotification(id++, receiverId),
                fillOutApplicationFormNotification(id++, receiverId),
                officerRejectApplicationNotification(id++, receiverId),
                participantClubRoleChangedNotification(id, receiverId)
        );
    }
}
