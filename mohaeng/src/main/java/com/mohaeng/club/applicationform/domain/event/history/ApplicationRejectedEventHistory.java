package com.mohaeng.club.applicationform.domain.event.history;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ApplicationRejectedEvent")
public class ApplicationRejectedEventHistory extends BaseEventHistory {

    private Long applicationFormId;
    private Long clubId;

    protected ApplicationRejectedEventHistory() {
    }

    public ApplicationRejectedEventHistory(final LocalDateTime eventDateTime,
                                           final Long applicationFormId,
                                           final Long clubId) {
        super(eventDateTime);
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public Long clubId() {
        return clubId;
    }
}