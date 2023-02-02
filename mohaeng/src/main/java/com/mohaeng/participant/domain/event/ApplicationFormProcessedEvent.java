package com.mohaeng.participant.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

/**
 * 모임 가입 신청 요청이 수락/거절 된 경우 발행
 */
public class ApplicationFormProcessedEvent extends NotificationEvent {

    private final Long applicationFormId;  // 가입 신청서 ID
    private final Long clubId;  // 모임 ID
    private final boolean isApproved;  // 수락됐는지 여부

    /**
     * @param receiverId 가입 혹은 거절된 회원 ID (Member ID)
     */
    private ApplicationFormProcessedEvent(final Object source,
                                          final Long receiverId,
                                          final Long applicationFormId,
                                          final Long clubId,
                                          final boolean isApproved) {
        super(source, receiverId);
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public static ApplicationFormProcessedEvent approve(final Object source,
                                                        final Long receiverId,
                                                        final Long applicationFormId,
                                                        final Long clubId) {
        return new ApplicationFormProcessedEvent(source, receiverId, applicationFormId, clubId, true);
    }

    public static ApplicationFormProcessedEvent reject(final Object source,
                                                       final Long receiverId,
                                                       final Long applicationFormId,
                                                       final Long clubId) {
        return new ApplicationFormProcessedEvent(source, receiverId, applicationFormId, clubId, false);
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
        return new ApplicationFormProcessedEventHistory(eventDateTime, applicationFormId, clubId, isApproved);
    }
}
