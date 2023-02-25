package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("(가입 신청서 처리 시, 관련된 알람 제거 핸들러)DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandler 은")
class DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandlerTest extends EventHandlerTest {

    private final NotificationRepository notificationRepository =
            mock(NotificationRepository.class);

    private final DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandler handler =
            new DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandler(eventHistoryRepository, notificationRepository);

    private final ApplicationProcessedEvent event =
            ApplicationProcessedEvent.reject(this, 1L, 1L, 1L);

    @Test
    void ApplicationProcessedEvent_를_받아_존재하는_가입_신청서_관련_알림을_제거한다() {
        // given
        given(notificationRepository.findApplicationProcessedNotificationByApplicationFormId(1L)).willReturn(List.of());
        willDoNothing().given(notificationRepository).deleteAllInBatch(any());

        // when
        handler.handle(event);

        // then
        verify(notificationRepository, times(1)).findApplicationProcessedNotificationByApplicationFormId(any());
        verify(notificationRepository, times(1)).deleteAllInBatch(any());
    }
}