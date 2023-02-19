package com.mohaeng.club.club.application.usecase;

public interface DeleteClubUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId
    ) {
    }
}
