package com.mohaeng.common;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.event.EventHistoryRepository;
import org.junit.jupiter.api.AfterEach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class EventHandlerTest {

    protected final EventHistoryRepository eventHistoryRepository = mock(EventHistoryRepository.class);

    /**
     * EventHandler 의 마지막에 process() 호출했는지 테스트
     */
    @AfterEach
    public void eventHistoryProcessedTest() {
        verify(eventHistoryRepository, times(1)).save(any(BaseEventHistory.class));
    }
}
