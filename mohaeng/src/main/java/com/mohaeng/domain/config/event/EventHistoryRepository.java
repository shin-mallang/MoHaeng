package com.mohaeng.domain.config.event;

public interface EventHistoryRepository {

    BaseEventHistory save(final BaseEventHistory baseEventHistory);
}
