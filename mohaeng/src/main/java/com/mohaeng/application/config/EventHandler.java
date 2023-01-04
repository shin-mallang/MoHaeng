package com.mohaeng.application.config;

import com.mohaeng.domain.config.event.BaseEvent;
import com.mohaeng.domain.config.event.BaseEventHistory;
import com.mohaeng.domain.config.event.EventHistoryRepository;

public abstract class EventHandler<EVENT extends BaseEvent> {

    protected final EventHistoryRepository eventHistoryRepository;

    protected EventHandler(final EventHistoryRepository eventHistoryRepository) {
        this.eventHistoryRepository = eventHistoryRepository;
    }

    /**
     * 마지막에 process() 를 반드시 호출
     */
    public abstract void handle(final EVENT event);

    /**
     * handle 이후 반드시 호출
     */
    public void process(final EVENT event) {
        BaseEventHistory history = event.history();
        history.process();
        eventHistoryRepository.save(history);
    }
}
