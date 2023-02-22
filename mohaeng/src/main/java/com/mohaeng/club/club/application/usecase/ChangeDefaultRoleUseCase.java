package com.mohaeng.club.club.application.usecase;

public interface ChangeDefaultRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long clubRoleId
    ) {
    }
}
