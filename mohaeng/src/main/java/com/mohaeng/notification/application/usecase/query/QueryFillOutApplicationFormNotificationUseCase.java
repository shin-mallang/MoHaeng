package com.mohaeng.notification.application.usecase.query;

import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryFillOutApplicationFormNotificationUseCase {

    /**
     * 가입 신청 요청 알림은, 읽음 처리되지 않는다.
     * <p>
     * 오직 수락 혹은 거절을 통해서만 제거된다.
     */
    Page<FillOutApplicationFormNotificationDto> query(final Query query);

    record Query(
            Long memberId,
            Pageable pageable
    ) {
    }
}
