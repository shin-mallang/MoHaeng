package com.mohaeng.participant.application.service;

import com.mohaeng.common.event.Events;
import com.mohaeng.participant.application.usecase.ExpelParticipantUseCase;
import com.mohaeng.participant.domain.event.ExpelParticipantEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import com.mohaeng.participant.exception.ParticipantExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class ExpelParticipant implements ExpelParticipantUseCase {

    private final ParticipantRepository participantRepository;

    public ExpelParticipant(final ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        // 요청자 조회
        Participant requester = participantRepository.findWithMemberAndClubById(command.requesterParticipantId())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 추방 대상 조회
        Participant target = participantRepository.findWithMemberAndClubById(command.targetParticipantId())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 요청한 Member 가 Participant와 일치하는지 확인
        validateParticipantIsRequester(command.requesterMemberId(), requester);

        // 모임에서 추방
        requester.expelFromClub(target);

        // 참여자 정보 제거
        participantRepository.delete(target);

        // 추방 이벤트 발행 -> 추방되었다는 알림 보내기
        Events.raise(new ExpelParticipantEvent(this, target.member().id(), target.club().id()));
    }

    /**
     * 요청한 Member 가 Participant와 일치하는지 확인한다.
     * <p>
     * TODO : LeaveParticipant 와 중복 코드
     */
    private void validateParticipantIsRequester(final Long memberId, final Participant participant) {
        if (!participant.member().id().equals(memberId)) {
            throw new ParticipantException(ParticipantExceptionType.MISMATCH_BETWEEN_PARTICIPANT_AND_MEMBER);
        }
    }
}
