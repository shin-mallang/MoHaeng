package com.mohaeng.clubrole.application.usecase;

/**
 * 모임 역할 제거
 */
public interface DeleteClubRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubRoleId  // 제거할 역할 ID
    ) {
    }
}
