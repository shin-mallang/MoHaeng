package com.mohaeng.club.club.application.usecase.command;

public interface CreateClubUseCase {

    Long command(final Command command);

    record Command(
            Long memberId,
            String name,
            String description,
            int maxPeopleCount
    ) {
    }
}
