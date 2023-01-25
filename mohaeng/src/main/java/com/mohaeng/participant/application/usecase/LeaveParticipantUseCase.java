package com.mohaeng.participant.application.usecase;

/**
 * 참가자가 모임에서 탈퇴시 사용.
 */
public interface LeaveParticipantUseCase {

    /**
     * 회원 id와, 참여자 id를 받아
     * 일치한다면 해당 모임에서 회원을 탈퇴시킨다.
     */
    void command(final Command command);

    record Command(
            Long memberId,
            Long participantId
    ) {
    }
}
