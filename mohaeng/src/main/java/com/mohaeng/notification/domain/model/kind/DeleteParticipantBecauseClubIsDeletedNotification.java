package com.mohaeng.notification.domain.model.kind;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.kind.DeleteParticipantBecauseClubIsDeletedNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 모임이 삭제되어 해당 모임에서 제거되었을 때,
 * 기존 모임의 참여자들에게 알려주기 위한 알림
 * <p>
 * 참고 - Club은 제거될 것이므로 ClubId를 통해서 할 수 있는것이 없다.
 * 따라서 ClubId 대신 Club 의 이름과 설명을 통해 어떤 모임이 제거되었는지 알 수 있도록 구현하였다.
 */
@DiscriminatorValue(value = "DeleteParticipantBecauseClubIsDeletedNotification")
@Entity
public class DeleteParticipantBecauseClubIsDeletedNotification extends Notification {

    private String clubName;  // 제거된 모임 이름
    private String clubDescription;  // 제거된 모임의 설명

    protected DeleteParticipantBecauseClubIsDeletedNotification() {
    }

    public DeleteParticipantBecauseClubIsDeletedNotification(final Receiver receiver,
                                                             final String clubName,
                                                             final String clubDescription) {
        super(receiver);
        this.clubName = clubName;
        this.clubDescription = clubDescription;
    }

    public String clubName() {
        return clubName;
    }

    public String clubDescription() {
        return clubDescription;
    }

    @Override
    public NotificationDto toDto() {
        return new DeleteParticipantBecauseClubIsDeletedNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                clubName(),
                clubDescription()
        );
    }
}
