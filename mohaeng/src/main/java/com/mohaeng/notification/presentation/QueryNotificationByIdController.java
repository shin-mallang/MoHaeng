package com.mohaeng.notification.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.QueryNotificationByIdUseCase;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryNotificationByIdController {

    public static final String QUERY_ALARM_BY_ID_URL = "/api/alarm/{id}";

    private final QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    public QueryNotificationByIdController(final QueryNotificationByIdUseCase queryNotificationByIdUseCase) {
        this.queryNotificationByIdUseCase = queryNotificationByIdUseCase;
    }

    @GetMapping(path = QUERY_ALARM_BY_ID_URL)
    public ResponseEntity<NotificationResponse> queryById(
            @Auth Long memberId,
            @PathVariable("id") Long id
    ) {
        NotificationDto notificationDto = queryNotificationByIdUseCase.query(new QueryNotificationByIdUseCase.Query(id, memberId));

        return ResponseEntity.ok(notificationDto.toResponse());
    }
}
