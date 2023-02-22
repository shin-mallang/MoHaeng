package com.mohaeng.notification.domain.model.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.DeleteParticipantCauseByClubDeletedNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 모임이 삭제되어 가입 신청서가 삭제되었을 때
 * 신청자들에게 알려주기 위한 알림
 * <p>
 * 참고 - Club은 제거될 것이므로 ClubId를 통해서 할 수 있는것이 없다.
 * 따라서 ClubId 대신 Club 의 이름과 설명을 통해 어떤 모임이 제거되었는지 알 수 있도록 구현하였다.
 */
@DiscriminatorValue(value = "DeleteParticipantCauseByClubDeletedNotification")
@Entity
public class DeleteParticipantCauseByClubDeletedNotification extends Notification {

    private String clubName;  // 제거된 모임 이름
    private String clubDescription;  // 제거된 모임의 설명

    protected DeleteParticipantCauseByClubDeletedNotification() {
    }

    public DeleteParticipantCauseByClubDeletedNotification(final Receiver receiver,
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
        return new DeleteParticipantCauseByClubDeletedNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                clubName(),
                clubDescription()
        );
    }
}