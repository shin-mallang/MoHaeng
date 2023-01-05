package com.mohaeng.club.application.mapper;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.model.Club;

public class ClubApplicationMapper {

    public static Club toDomainEntity(final CreateClubUseCase.Command command) {
        return new Club(command.name(),
                command.description(),
                command.maxPeopleCount());
    }
}
