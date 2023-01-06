package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("RequestJoinClubEvent")
public class RequestJoinClubEventHistory extends BaseEventHistory {

    private Long applicantId;  // 가입 신청자 ID
    private Long targetClubId;  // 모임 ID
    private Long applicationFormId;  // 가입 신청서 ID

    protected RequestJoinClubEventHistory() {
    }

    public RequestJoinClubEventHistory(final LocalDateTime eventDateTime,
                                       final Long applicantId,
                                       final Long targetClubId,
                                       final Long applicationFormId) {
        super(eventDateTime);
        this.applicantId = applicantId;
        this.targetClubId = targetClubId;
        this.applicationFormId = applicationFormId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long targetClubId() {
        return targetClubId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }
}
