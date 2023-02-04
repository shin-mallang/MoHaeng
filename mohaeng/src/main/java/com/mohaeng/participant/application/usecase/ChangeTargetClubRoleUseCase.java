package com.mohaeng.participant.application.usecase;

/**
 * 대상의 역할을 변경
 */
public interface ChangeTargetClubRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,  // 요청자 회원 ID
            Long targetParticipantId,  // 역할을 변경할 대상의 참가자 ID
            Long clubRoleId  // 변경하고자 하는 역할의 ID
    ) {
    }
}
