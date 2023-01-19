package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ClubJoinApplicationRequestedEvent")
public class ClubJoinApplicationCreatedEventHistory extends BaseEventHistory {

    private Long clubId;  // 가입을 요청한 모임 ID

    private Long applicantId;  // 가입 신청자의 Member ID

    private Long applicationFormId;  // 가입 신청서 ID

    protected ClubJoinApplicationCreatedEventHistory() {
    }

    public ClubJoinApplicationCreatedEventHistory(final LocalDateTime eventDateTime,
                                                  final Long clubId,
                                                  final Long applicantId,
                                                  final Long applicationFormId) {
        super(eventDateTime);
        this.clubId = clubId;
        this.applicantId = applicantId;
        this.applicationFormId = applicationFormId;
    }

    public Long applicantId() {
        return applicantId;
    }

    public Long clubId() {
        return clubId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }
}
