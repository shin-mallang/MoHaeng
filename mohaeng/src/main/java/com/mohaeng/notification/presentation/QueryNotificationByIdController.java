package com.mohaeng.notification.presentation;

import com.mohaeng.notification.application.usecase.QueryNotificationByIdUseCase;
import com.mohaeng.notification.application.usecase.dto.NotificationDto;
import com.mohaeng.notification.presentation.mapper.NotificationControllerMapper;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class QueryNotificationByIdController {

    public static final String QUERY_ALARM_BY_ID = "/api/alarm/{id}";

    private final QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    public QueryNotificationByIdController(final QueryNotificationByIdUseCase queryNotificationByIdUseCase) {
        this.queryNotificationByIdUseCase = queryNotificationByIdUseCase;
    }

    @GetMapping(path = QUERY_ALARM_BY_ID)
    public ResponseEntity<NotificationResponse> queryById(
            @Auth Long memberId,
            @PathVariable("id") Long id
    ) {
        NotificationDto notificationDto = queryNotificationByIdUseCase.query(new QueryNotificationByIdUseCase.Query(id, memberId));

        return ResponseEntity.ok(NotificationControllerMapper.toResponseDto(notificationDto));
    }

    public record NotificationResponse(
            LocalDateTime createdAt,  // 알람 발송일
            String title,
            String content,
            String alarmType,
            boolean isRead  // 알람 읽음 여부
    ) {
    }
}
