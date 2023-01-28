package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.event.Events;
import com.mohaeng.participant.domain.event.DeleteClubParticipantEvent;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 모임을 제거하기 위해, 모임 제거 이벤트를 받으면
 * 해당 모임의 참여자와 해당 모임의 역할을 모두 삭제하는 핸들러
 * <p>
 * (Participant는 ClubRole에 대해 의존성을 가지고 있기 때문에, 따로 이벤트를 발행하지 않고 이곳에서 한번에 처리한다.)
 */
@Component
public class DeleteParticipantAndClubRoleWithDeleteClubEventHandler extends EventHandler<DeleteClubEvent> {

    private final ParticipantRepository participantRepository;
    private final ClubRoleRepository clubRoleRepository;

    protected DeleteParticipantAndClubRoleWithDeleteClubEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                                     final ParticipantRepository participantRepository,
                                                                     final ClubRoleRepository clubRoleRepository) {
        super(eventHistoryRepository);
        this.participantRepository = participantRepository;
        this.clubRoleRepository = clubRoleRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final DeleteClubEvent event) {

        // 대상자 조회
        List<Long> receiverIds = participantRepository.findAllWithMemberByClubId(event.clubId())
                .stream()
                .map(it -> it.member().id())
                .toList();

        // 참여자 제거
        participantRepository.deleteAllByClubId(event.clubId());

        // 모임의 역할 제거
        clubRoleRepository.deleteAllByClubId(event.clubId());

        // 참여자 제거 이벤트 발행 -> 참여자들에게 모임이 제거되었다는 알림 전송
        Events.raise(new DeleteClubParticipantEvent(this, receiverIds, event.clubId()));

        process(event);
    }
}
