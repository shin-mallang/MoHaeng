package com.mohaeng.participant.application.service;

import com.mohaeng.clubrole.application.usecase.ChangeClubRoleNameUseCase;
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

@Transactional
@Service
public class ChangeClubRoleName implements ChangeClubRoleNameUseCase {

    private final ClubRoleRepository clubRoleRepository;
    private final ParticipantRepository participantRepository;

    public ChangeClubRoleName(final ClubRoleRepository clubRoleRepository,
                              final ParticipantRepository participantRepository) {
        this.clubRoleRepository = clubRoleRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        ClubRole clubRole = clubRoleRepository.findWithClubById(command.clubRoleId())
                .orElseThrow(() -> new ClubRoleException(NOT_FOUND_CLUB_ROLE));

        Participant participant = participantRepository.findWithClubRoleByMemberIdAndClubId(command.memberId(), clubRole.club().id())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 역할 이름 변경
        participant.changeClubRoleName(clubRole, command.roleName());
    }
}
