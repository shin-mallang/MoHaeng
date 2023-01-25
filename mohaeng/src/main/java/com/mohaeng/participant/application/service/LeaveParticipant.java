package com.mohaeng.participant.application.service;

import com.mohaeng.participant.application.usecase.LeaveParticipantUseCase;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import com.mohaeng.participant.exception.ParticipantExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class LeaveParticipant implements LeaveParticipantUseCase {

    private final ParticipantRepository participantRepository;

    public LeaveParticipant(final ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        // 참여자 조회
        Participant participant = participantRepository.findWithMemberAndClubById(command.participantId())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 요청한 Member 가 Participant와 일치하는지 확인
        validateParticipantIsRequester(command.memberId(), participant);

        // 모임에서 탈퇴
        participant.leaveFromClub();

        participantRepository.delete(participant);
    }

    private void validateParticipantIsRequester(final Long memberId, final Participant participant) {
        if (!participant.member().id().equals(memberId)) {
            throw new ParticipantException(ParticipantExceptionType.MISMATCH_BETWEEN_PARTICIPANT_AND_MEMBER);
        }
    }
}
