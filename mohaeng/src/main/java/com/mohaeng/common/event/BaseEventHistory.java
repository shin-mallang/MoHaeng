package com.mohaeng.common.event;

import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", length = 60)
public abstract class BaseEventHistory extends BaseEntity {

    protected LocalDateTime eventDateTime;  // 이벤트 발행 시간

    protected boolean processed;  // 처리 상태

    protected BaseEventHistory() {
    }

    public BaseEventHistory(final LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public LocalDateTime eventDateTime() {
        return eventDateTime;
    }

    public boolean processed() {
        return processed;
    }

    /**
     * 이벤트 처리
     */
    public void process() {
        this.processed = true;
    }
}
