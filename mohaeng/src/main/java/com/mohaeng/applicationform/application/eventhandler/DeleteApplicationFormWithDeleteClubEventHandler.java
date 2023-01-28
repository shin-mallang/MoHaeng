package com.mohaeng.applicationform.application.eventhandler;

import com.mohaeng.applicationform.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.event.Events;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 모임을 제거하기 위해, 모임 제거 이벤트를 받으면
 * 해당 모임에 존재하는 가입 신청서를 모두 제거하는 핸들러
 */
@Component
public class DeleteApplicationFormWithDeleteClubEventHandler extends EventHandler<DeleteClubEvent> {

    private final ApplicationFormRepository applicationFormRepository;

    protected DeleteApplicationFormWithDeleteClubEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                              final ApplicationFormRepository applicationFormRepository) {
        super(eventHistoryRepository);
        this.applicationFormRepository = applicationFormRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final DeleteClubEvent event) {

        List<Long> receiverIds = applicationFormRepository.findAllWithApplicantByTargetIdAndProcessedFalse(event.clubId())
                .stream()
                .map(it -> it.applicant().id())
                .toList();

        // 가입 신청서 제거
        applicationFormRepository.deleteAllByClubId(event.clubId());

        // 가입 신청서 제거 이벤트 발행 -> 신청자들에게 모임이 제거되어 가입 신청서가 제거되었다는 알림 전송
        Events.raise(new DeleteApplicationFormEvent(this, receiverIds, event.clubName(), event.clubDescription()));

        process(event);
    }
}
