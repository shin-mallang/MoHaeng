package com.mohaeng.notification.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.common.presentation.query.PageResponseAssembler;
import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryFillOutApplicationFormNotificationUseCase;
import com.mohaeng.notification.presentation.response.type.FillOutApplicationFormNotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryFillOutApplicationFormNotificationController {

    public static final String QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL = "/api/notification/fill-out-application-form";

    private final QueryFillOutApplicationFormNotificationUseCase queryFillOutApplicationFormNotificationUseCase;

    public QueryFillOutApplicationFormNotificationController(final QueryFillOutApplicationFormNotificationUseCase queryFillOutApplicationFormNotificationUseCase) {
        this.queryFillOutApplicationFormNotificationUseCase = queryFillOutApplicationFormNotificationUseCase;
    }

    @GetMapping(QUERY_FILL_OUT_APPLICATION_FORM_NOTIFICATION_URL)
    public ResponseEntity<CommonResponse<List<FillOutApplicationFormNotificationResponse>>> query(
            @Auth final Long memberId,
            @PageableDefault(size = 10) final Pageable pageable
    ) {
        Page<FillOutApplicationFormNotificationDto> result = queryFillOutApplicationFormNotificationUseCase.query(
                new QueryFillOutApplicationFormNotificationUseCase.Query(memberId, pageable)
        );
        return ResponseEntity.ok(PageResponseAssembler.assemble(result.map(FillOutApplicationFormNotificationDto::toResponse)));
    }
}
