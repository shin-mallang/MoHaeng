package com.mohaeng.notification.application.usecase.query;

import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryFillOutApplicationFormNotificationUseCase {

    /**
     * 가입 신청 요청 알림은 읽음 여부에 관계없이 존재하면 모두 조회한다.
     * (존재한다는 것은 아직 처리되지 않았다는 상태이다)
     * 이는 오직 수락 혹은 거절을 통해서만 제거된다.
     */
    Page<FillOutApplicationFormNotificationDto> query(final Query query);

    record Query(
            Long memberId,
            Pageable pageable
    ) {
    }
}
