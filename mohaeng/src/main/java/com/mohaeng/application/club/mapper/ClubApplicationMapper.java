package com.mohaeng.application.club.mapper;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.domain.Club;

public class ClubApplicationMapper {

    public static Club toDomainEntity(final CreateClubUseCase.Command command) {
        return new Club(
                command.name(),
                command.description(),
                command.maxPeopleCount()
        );
    }
}
