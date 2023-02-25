package com.mohaeng.common.fixtures;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.*;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_DESCRIPTION;
import static com.mohaeng.common.fixtures.ClubFixture.ANA_NAME;

public class NotificationFixture {

    public static OfficerApproveApplicationNotification officerApproveApplicationNotification() {
        return new OfficerApproveApplicationNotification(Receiver.of(1L), 2L, 3L, 4L, 5L);
    }

    public static ApplicationProcessedNotification applicationProcessedNotification() {
        return new ApplicationProcessedNotification(Receiver.of(1L), 2L, true);
    }

    public static DeleteApplicationFormCauseByClubDeletedNotification deleteApplicationFormCauseByClubDeletedNotification() {
        return new DeleteApplicationFormCauseByClubDeletedNotification(Receiver.of(1L), ANA_NAME, ANA_DESCRIPTION);
    }

    public static DeleteParticipantCauseByClubDeletedNotification deleteParticipantCauseByClubDeletedNotification() {
        return new DeleteParticipantCauseByClubDeletedNotification(Receiver.of(1L), ANA_NAME, ANA_DESCRIPTION);
    }

    public static ExpelParticipantNotification expelParticipantNotification() {
        return new ExpelParticipantNotification(Receiver.of(1L), 2L);
    }

    public static FillOutApplicationFormNotification fillOutApplicationFormNotification() {
        return new FillOutApplicationFormNotification(Receiver.of(1L), 2L, 3L, 4L);
    }

    public static OfficerRejectApplicationNotification officerRejectApplicationNotification() {
        return new OfficerRejectApplicationNotification(Receiver.of(1L), 2L, 3L, 4L);
    }

    public static ParticipantClubRoleChangedNotification participantClubRoleChangedNotification() {
        return new ParticipantClubRoleChangedNotification(Receiver.of(1L), 2L, 3L, "변경된 역할이름", OFFICER);
    }

    public static List<Notification> allKindNotifications() {
        return List.of(
                officerApproveApplicationNotification(),
                applicationProcessedNotification(),
                deleteApplicationFormCauseByClubDeletedNotification(),
                deleteParticipantCauseByClubDeletedNotification(),
                expelParticipantNotification(),
                fillOutApplicationFormNotification(),
                officerRejectApplicationNotification(),
                participantClubRoleChangedNotification()
        );
    }
}
