package com.mohaeng.club.club.application.usecase.command;

public interface ChangeDefaultRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long clubRoleId
    ) {
    }
}
