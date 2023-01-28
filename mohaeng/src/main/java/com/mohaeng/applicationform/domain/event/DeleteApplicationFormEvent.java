package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 모임이 삭제되어, 가입 신청서가 제거되었을때 발행됨
 */
public class DeleteApplicationFormEvent extends NotificationEvent {

    private final Long clubId;

    public DeleteApplicationFormEvent(final Object source,
                                      final List<Long> receiverIds,
                                      final Long clubId) {
        super(source, receiverIds);
        this.clubId = clubId;
    }

    @Override
    public BaseEventHistory history() {
        return new DeleteApplicationFormEventHistory(eventDateTime, clubId);
    }

    @Override
    public String toString() {
        return "DeleteApplicationFormEvent{" +
                "clubId=" + clubId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }
}
