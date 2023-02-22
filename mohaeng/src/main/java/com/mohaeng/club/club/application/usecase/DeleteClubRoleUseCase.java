package com.mohaeng.club.club.application.usecase;

public interface DeleteClubRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long clubRoleId
    ) {
    }
}
