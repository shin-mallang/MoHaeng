package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.LeaveClubUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class LeaveClub implements LeaveClubUseCase {

    private final ClubRepository clubRepository;

    public LeaveClub(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));
        Participant participant = club.findParticipantByMemberId(command.memberId()).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        club.deleteParticipant(participant);
    }
}
