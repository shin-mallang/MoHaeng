package com.mohaeng.club.applicationform.domain.event;

import com.mohaeng.club.applicationform.domain.event.history.ApplicationRejectedEventHistory;
import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

/**
 * 모임 가입 신청 요청이 거절된 경우 발행
 */
public class ApplicationRejectedEvent extends NotificationEvent {

    private final Long applicationFormId;  // 가입 신청서 ID
    private final Long clubId;  // 모임 ID

    /**
     * @param receiverId 가입 혹은 거절된 회원 ID (Member ID)
     */
    public ApplicationRejectedEvent(final Object source,
                                    final Long receiverId,
                                    final Long applicationFormId,
                                    final Long clubId) {
        super(source, receiverId);
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
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

    @Override
    public String toString() {
        return "ApplicationRejectedEvent{" +
                "applicationFormId=" + applicationFormId +
                ", clubId=" + clubId +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new ApplicationRejectedEventHistory(eventDateTime, applicationFormId, clubId);
    }
}