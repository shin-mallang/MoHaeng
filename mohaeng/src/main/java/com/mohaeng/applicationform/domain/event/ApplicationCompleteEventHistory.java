package com.mohaeng.applicationform.domain.event;

import com.mohaeng.applicationform.domain.model.enums.ApplicationProcessStatus;
import com.mohaeng.common.event.BaseEventHistory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class ApplicationCompleteEventHistory extends BaseEventHistory {

    private final Long applicationFormId;
    private final Long clubId;

    @Enumerated(EnumType.STRING)
    private final ApplicationProcessStatus status;

    protected ApplicationCompleteEventHistory() {
        this.applicationFormId = null;
        this.clubId = null;
        this.status = null;
    }

    public ApplicationCompleteEventHistory(final LocalDateTime eventDateTime,
                                           final Long applicationFormId,
                                           final Long clubId,
                                           final ApplicationProcessStatus status) {
        super(eventDateTime);
        this.applicationFormId = applicationFormId;
        this.clubId = clubId;
        this.status = status;
    }

    public Long applicationFormId() {
        return applicationFormId;
    }

    public Long clubId() {
        return clubId;
    }

    public ApplicationProcessStatus status() {
        return status;
    }
}
