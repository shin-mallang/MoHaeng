package com.mohaeng.participant.application.service;

import com.mohaeng.clubrole.application.usecase.ChangeDefaultRoleUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class ChangeDefaultRole implements ChangeDefaultRoleUseCase {

    private final ParticipantRepository participantRepository;
    private final ClubRoleRepository clubRoleRepository;

    public ChangeDefaultRole(final ParticipantRepository participantRepository, final ClubRoleRepository clubRoleRepository) {
        this.participantRepository = participantRepository;
        this.clubRoleRepository = clubRoleRepository;
    }

    @Override
    public void command(final Command command) {
        ClubRole defaultRoleCandidate = clubRoleRepository.findWithClubById(command.clubRoleId())
                .orElseThrow(() -> new ClubRoleException(NOT_FOUND_CLUB_ROLE));

        Participant participant = participantRepository.findWithClubRoleByMemberIdAndClub(command.memberId(), defaultRoleCandidate.club())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 기존 기본 역할
        ClubRole existingDefaultRole =
                clubRoleRepository.findDefaultRoleByClubAndClubRoleCategory(defaultRoleCandidate.club(), defaultRoleCandidate.clubRoleCategory());

        // 기본 역할 변경
        participant.changeDefaultRole(defaultRoleCandidate, existingDefaultRole);
    }
}