package com.mohaeng.participant.application.service;

import com.mohaeng.clubrole.application.usecase.ChangeTargetClubRoleUseCase;
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
public class ChangeTargetClubRole implements ChangeTargetClubRoleUseCase {

    private final ParticipantRepository participantRepository;
    private final ClubRoleRepository clubRoleRepository;

    public ChangeTargetClubRole(final ParticipantRepository participantRepository,
                                final ClubRoleRepository clubRoleRepository) {
        this.participantRepository = participantRepository;
        this.clubRoleRepository = clubRoleRepository;
    }

    @Override
    public void command(final Command command) {
        Participant target = participantRepository.findWithClubAndClubRoleById(command.targetParticipantId())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        ClubRole clubRole = clubRoleRepository.findById(command.clubRoleId())
                .orElseThrow(() -> new ClubRoleException(NOT_FOUND_CLUB_ROLE));
        Participant requester = participantRepository.findWithClubRoleByMemberIdAndClub(command.memberId(), target.club())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        requester.changeTargetRole(target, clubRole);
    }
}
