package com.mohaeng.application.club.usecase;

public interface CreateClubUseCase {

    Long command(Command command);

    record Command(String name,
                   String description,
                   int maxPeopleCount) {
    }
}
