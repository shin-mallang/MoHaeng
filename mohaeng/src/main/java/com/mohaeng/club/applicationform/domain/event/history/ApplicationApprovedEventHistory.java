package com.mohaeng.club.applicationform.domain.event.history;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ApplicationApprovedEvent")
public class ApplicationApprovedEventHistory extends BaseEventHistory {

    private Long applicationFormId;
    private Long clubId;

    protected ApplicationApprovedEventHistory() {
    }

    public ApplicationApprovedEventHistory(final LocalDateTime eventDateTime,
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