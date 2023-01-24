package com.mohaeng.participant.application.usecase;

/**
 * 회원 추방 기능
 */
public interface ExpelParticipantUseCase {

    /**
     * 참여자를 모임에서 추방시킨다.
     * 회장만이 모임에서 참여자를 추방시킬 수 있다.
     */
    void command(final Command command);

    record Command(
            Long requesterMemberId,  // 추방 요청을 보낸 회원 ID
            Long requesterParticipantId,  // 추방 요청을 보낸 회원의 참가자 ID
            Long targetParticipantId  // 추방 대상 참가자 ID
    ) {
    }
}
