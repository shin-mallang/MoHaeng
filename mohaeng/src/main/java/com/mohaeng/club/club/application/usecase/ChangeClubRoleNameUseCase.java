package com.mohaeng.club.club.application.usecase;

/**
 * 모임 역할 이름 변경
 */
public interface ChangeClubRoleNameUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long clubRoleId,
            String roleName
    ) {
    }
}