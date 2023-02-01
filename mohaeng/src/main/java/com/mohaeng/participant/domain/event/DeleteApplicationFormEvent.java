package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 모임이 삭제되어, 가입 신청서가 제거되었을때 발행됨
 * <p>
 * 신청자들에게 해당 모임이 제거되었다는 알림 발행
 */
public class DeleteApplicationFormEvent extends NotificationEvent {

    private final String clubName;
    private final String clubDescription;

    public DeleteApplicationFormEvent(final Object source,
                                      final List<Long> receiverIds,
                                      final String clubName,
                                      final String clubDescription) {
        super(source, receiverIds);
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
    public BaseEventHistory history() {
        return new DeleteApplicationFormEventHistory(eventDateTime, clubName, clubDescription);
    }

    @Override
    public String toString() {
        return "DeleteApplicationFormEvent{" +
                "clubName='" + clubName + '\'' +
                ", clubDescription='" + clubDescription + '\'' +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}
