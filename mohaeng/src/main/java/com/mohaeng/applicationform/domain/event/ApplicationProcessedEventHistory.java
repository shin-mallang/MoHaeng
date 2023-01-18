package com.mohaeng.applicationform.domain.event;

import com.mohaeng.common.event.BaseEventHistory;

import java.time.LocalDateTime;

public class ApplicationProcessedEventHistory extends BaseEventHistory {

    private final Long applicationFormId;
    private final Long clubId;

    private final boolean isApproved;

    protected ApplicationProcessedEventHistory() {
        this.applicationFormId = null;
        this.clubId = null;
        this.isApproved = false;
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
