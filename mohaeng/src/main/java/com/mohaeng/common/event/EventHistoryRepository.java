package com.mohaeng.common.event;

public interface EventHistoryRepository {

    BaseEventHistory save(final BaseEventHistory baseEventHistory);
}
