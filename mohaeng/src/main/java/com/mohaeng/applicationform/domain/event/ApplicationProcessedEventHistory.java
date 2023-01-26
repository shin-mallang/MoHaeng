package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ApplicationProcessedEvent")
public class ApplicationProcessedEventHistory extends BaseEventHistory {

    private Long applicationFormId;
    private Long clubId;
    private boolean isApproved;

    protected ApplicationProcessedEventHistory() {
    }

    public ApplicationProcessedEventHistory(final LocalDateTime eventDateTime,
                                            final Long applicationFormId,
                                            final Long clubId,
                                            final boolean isApproved) {
        super(eventDateTime);
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public Long clubId() {
        return clubId;
    }

    public boolean isApproved() {
        return isApproved;
    }
}
