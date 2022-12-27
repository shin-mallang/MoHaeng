package com.mohaeng.application.club.service;

import com.mohaeng.application.club.mapper.ClubApplicationMapper;
import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;

public class CreateClub implements CreateClubUseCase {

    private final ClubCommand clubCommand;

    public CreateClub(final ClubCommand clubCommand) {
        this.clubCommand = clubCommand;
    }

    @Override
    public Long command(final Command command) {
        Club club = ClubApplicationMapper.toDomainEntity(command);
        return clubCommand.save(club);
    }
}
