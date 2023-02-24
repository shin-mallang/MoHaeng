package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.usecase.DeleteClubRoleUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteClubRole implements DeleteClubRoleUseCase {

    private final ClubRepository clubRepository;

    public DeleteClubRole(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public void command(final Command command) {
        Club club = clubRepository.findById(command.clubId()).orElseThrow(() -> new ClubException(ClubExceptionType.NOT_FOUND_CLUB));
        club.deleteRole(command.memberId(), command.clubRoleId());
    }
}
