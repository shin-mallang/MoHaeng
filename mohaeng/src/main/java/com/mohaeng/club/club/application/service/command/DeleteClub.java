package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.DeleteClubUseCase;
import com.mohaeng.club.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.event.Events;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;

@Service
@Transactional
public class DeleteClub implements DeleteClubUseCase {

    private final ClubRepository clubRepository;

    public DeleteClub(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));

        // 요청자가 회장인지 확인
        validateRequesterIsPresident(command.memberId(), club.findPresident());

        // 모임 제거 이벤트 발행 -> 가입 신청서 & 참여자 제거 -> 모임 역할 제거 + 모임 제거 알림 전송(- AfterCommit 으로)
        Events.raise(new DeleteClubEvent(this, receiverIds(club), club.id(), club.name(), club.description()));

        clubRepository.delete(club);
    }

    /**
     * 모임에 참여중인 참가자들의 Member Id
     */
    private List<Long> receiverIds(final Club club) {
        return club.findAllParticipant().stream().map(it -> it.member().id()).toList();
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
