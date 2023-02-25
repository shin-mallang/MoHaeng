package com.mohaeng.club.club.application.usecase.command;

/**
 * 모임에서 탈퇴
 */
public interface LeaveClubUseCase {

    /**
     * 회원 id와, 모임 id를 받아
     * 해당 모임에서 회원을 탈퇴시킨다.
     */
    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId
    ) {
    }
}
