package com.mohaeng.participant.application.service;

import com.mohaeng.clubrole.application.usecase.CreateClubRoleUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class CreateClubRole implements CreateClubRoleUseCase {

    private final ClubRoleRepository clubRoleRepository;
    private final ParticipantRepository participantRepository;

    public CreateClubRole(final ClubRoleRepository clubRoleRepository,
                          final ParticipantRepository participantRepository) {
        this.clubRoleRepository = clubRoleRepository;
        this.participantRepository = participantRepository;

    }

    @Override
    public Long command(final Command command) {
        Participant participant = participantRepository.findWithClubRoleByMemberIdAndClubId(command.memberId(), command.clubId())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        ClubRole created = participant.createClubRole(command.name(), command.clubRoleCategory());
        return clubRoleRepository.save(created).id();
    }
}
