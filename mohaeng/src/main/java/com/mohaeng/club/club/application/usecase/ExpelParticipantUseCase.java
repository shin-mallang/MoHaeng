package com.mohaeng.club.club.application.usecase;

/**
 * 회원 추방 기능
 */
public interface ExpelParticipantUseCase {

    /**
     * 참여자를 모임에서 추방시킨다.
     * 회장만이 모임에서 다른 참여자를 추방시킬 수 있다.
     */
    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long targetParticipantId
    ) {
    }
}
