package com.mohaeng.notification.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.common.presentation.query.PageResponseAssembler;
import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryAllNotificationUseCase;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter;
import com.mohaeng.notification.presentation.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mohaeng.notification.domain.repository.NotificationQueryRepository.NotificationFilter.ReadFilter.ALL;

@RestController
public class QueryAllNotificationController {

    public static final String QUERY_ALL_NOTIFICATION_URL = "/api/notification";

    private final QueryAllNotificationUseCase queryAllNotificationUseCase;

    public QueryAllNotificationController(final QueryAllNotificationUseCase queryAllNotificationUseCase) {
        this.queryAllNotificationUseCase = queryAllNotificationUseCase;
    }

    @GetMapping(QUERY_ALL_NOTIFICATION_URL)
    public ResponseEntity<CommonResponse<List<NotificationResponse>>> query(
            @Auth final Long memberId,
            @ModelAttribute final ClubSearchRequest clubSearchRequest,
            @PageableDefault(size = 10) final Pageable pageable
    ) {
        Page<NotificationDto> result = queryAllNotificationUseCase.query(
                clubSearchRequest.toQuery(memberId, pageable)
        );
        return ResponseEntity.ok(PageResponseAssembler.assemble(result.map(NotificationDto::toResponse)));
    }

    public record ClubSearchRequest(
            NotificationFilter.ReadFilter readFilter
    ) {
        public QueryAllNotificationUseCase.Query toQuery(final Long memberId, final Pageable pageable) {
            if (readFilter == null) {
                return new QueryAllNotificationUseCase.Query(new NotificationFilter(memberId, ALL), pageable);
            }
            return new QueryAllNotificationUseCase.Query(new NotificationFilter(memberId, readFilter), pageable);
        }
    }
}
