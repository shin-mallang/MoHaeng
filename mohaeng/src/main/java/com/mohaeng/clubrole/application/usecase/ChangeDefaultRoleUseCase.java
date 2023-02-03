package com.mohaeng.clubrole.application.usecase;

/**
 * 모임의 기본 역할 변경
 */
public interface ChangeDefaultRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubRoleId
    ) {
    }
}
