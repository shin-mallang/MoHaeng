package com.mohaeng.notification.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryNotificationByIdUseCase;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryNotificationByIdController {

    public static final String QUERY_NOTIFICATION_BY_ID_URL = "/api/notification/{notificationId}";

    private final QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    public QueryNotificationByIdController(final QueryNotificationByIdUseCase queryNotificationByIdUseCase) {
        this.queryNotificationByIdUseCase = queryNotificationByIdUseCase;
    }

    @GetMapping(QUERY_NOTIFICATION_BY_ID_URL)
    public ResponseEntity<CommonResponse<NotificationResponse>> query(
            @Auth final Long memberId,
            @PathVariable(name = "notificationId") final Long notificationId
    ) {
        NotificationDto query = queryNotificationByIdUseCase.query(
                new QueryNotificationByIdUseCase.Query(memberId, notificationId)
        );
        return ResponseEntity.ok(CommonResponse.from(query.toResponse()));
    }
}
