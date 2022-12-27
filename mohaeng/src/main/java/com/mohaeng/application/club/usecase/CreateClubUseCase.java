package com.mohaeng.application.club.usecase;

public interface CreateClubUseCase {

    Long command(final Command command);

    record Command(
            Long presidentId,
            String name,
            String description,
            int maxPeopleCount
    ) {
    }
}
