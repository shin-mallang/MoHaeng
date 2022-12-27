package com.mohaeng.application.club.usecase;

public interface CreateClubUseCase {

    Long command(final Command command);

    record Command(
       String name,
       String description,
       int maxPeopleCount
    ) {
    }
}
