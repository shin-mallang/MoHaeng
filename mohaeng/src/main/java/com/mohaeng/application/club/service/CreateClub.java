package com.mohaeng.application.club.service;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateClub implements CreateClubUseCase {

    private final ClubCommand clubCommand;

    public CreateClub(final ClubCommand clubCommand) {
        this.clubCommand = clubCommand;
    }

    @Override
    public Long command(final Command command) {
        Club club = Club.newClub(command.name(), command.description(), command.maxPeopleCount(), command.presidentId());
        return clubCommand.save(club);
    }
}
