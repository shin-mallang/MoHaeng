package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.command.DelegatePresidentUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;

@Service
@Transactional
public class DelegatePresident implements DelegatePresidentUseCase {

    private final ClubRepository clubRepository;

    public DelegatePresident(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));
        club.delegatePresident(command.memberId(), command.presidentCandidateParticipantId());
    }
}
