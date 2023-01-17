package com.mohaeng.applicationform.domain.event;

import com.mohaeng.applicationform.domain.model.enums.ApplicationProcessStatus;
import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.common.event.BaseEventHistory;

import java.util.List;

/**
 * 모임 가입 신청 요청이 수락/거절 된 경우 발행
 */
public class ApplicationCompleteEvent extends AlarmEvent {

    private final Long applicationFormId;
    private final Long clubId;
    private final ApplicationProcessStatus status;

    /**
     * @param receiverId 가입 혹은 거절된 회원 ID (Member ID)
     */
    public ApplicationCompleteEvent(final Object source,
                                    final Long receiverId,
                                    final Long applicationFormId,
                                    final Long clubId,
                                    final ApplicationProcessStatus status) {
        super(source, List.of(receiverId));
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ApplicationCompleteEvent{" +
                "applicationFormId=" + applicationFormId +
                ", clubId=" + clubId +
                ", status=" + status +
                ", receiverIds=" + receiverIds +
                ", eventDateTime=" + eventDateTime +
                ", source=" + source +
                '}';
    }

    @Override
    public BaseEventHistory history() {
        return new ApplicationCompleteEventHistory(eventDateTime, applicationFormId, clubId, status);
    }

    public Long clubId() {
        return clubId;
    }
}
