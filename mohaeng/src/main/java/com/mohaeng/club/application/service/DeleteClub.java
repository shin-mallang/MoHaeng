package com.mohaeng.club.application.service;

import com.mohaeng.club.application.usecase.DeleteClubUseCase;
import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.event.Events;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;

@Transactional
@Service
public class DeleteClub implements DeleteClubUseCase {

    private final ClubRepository clubRepository;
    private final ParticipantRepository participantRepository;

    public DeleteClub(final ClubRepository clubRepository, final ParticipantRepository participantRepository) {
        this.clubRepository = clubRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));
        Participant president = participantRepository.findPresidentWithMemberByClub(club).orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));

        // 요청자가 회장인지 확인
        validateRequesterIsPresident(command.memberId(), president);

        // 모임 제거 이벤트 발행 -> 가입 신청서 & 참여자 제거 -> 모임 역할 제거 + 모임 제거 알림 전송(- AfterCommit 으로)
        Events.raise(new DeleteClubEvent(this, club.id()));

        clubRepository.delete(club);
    }

    /**
     * 요청자가 회장인지 확인한다.
     */
    private void validateRequesterIsPresident(final Long memberId, final Participant president) {
        if (!president.member().id().equals(memberId)) {
            throw new ClubException(NO_AUTHORITY_DELETE_CLUB);
        }
    }
}
