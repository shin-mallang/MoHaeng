package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.command.CreateClubRoleUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;

@Service
@Transactional
public class CreateClubRole implements CreateClubRoleUseCase {

    private final ClubRepository clubRepository;

    public CreateClubRole(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));
        club.createRole(command.memberId(), command.name(), command.clubRoleCategory());
    }
}
