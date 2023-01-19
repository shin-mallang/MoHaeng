package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

/**
 * 모임 가입 신청 요청이 수락/거절 된 경우 발행
 */
public class ApplicationProcessedEvent extends NotificationEvent {

    private final Long applicationFormId;
    private final Long clubId;
    private final boolean isApproved;

    /**
     * @param receiverId 가입 혹은 거절된 회원 ID (Member ID)
     */
    private ApplicationProcessedEvent(final Object source,
                                      final Long receiverId,
                                      final Long applicationFormId,
                                      final Long clubId,
                                      final boolean isApproved) {
        super(source, List.of(receiverId));
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public static ApplicationProcessedEvent approve(final Object source,
                                                    final Long receiverId,
                                                    final Long applicationFormId,
                                                    final Long clubId) {
        return new ApplicationProcessedEvent(source, receiverId, applicationFormId, clubId, true);
    }

    public static ApplicationProcessedEvent reject(final Object source,
                                                   final Long receiverId,
                                                   final Long applicationFormId,
                                                   final Long clubId) {
        return new ApplicationProcessedEvent(source, receiverId, applicationFormId, clubId, false);
    }

    public Long clubId() {
        return clubId;
    }

    public Long receiverId() {
        return receiverIds.get(0);
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public String toString() {
        return "ApplicationCompleteEvent{" +
                "applicationFormId=" + applicationFormId +
                ", clubId=" + clubId +
                ", approved=" + isApproved +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new ApplicationProcessedEventHistory(eventDateTime, applicationFormId, clubId, isApproved);
    }
}
