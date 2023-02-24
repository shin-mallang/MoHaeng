package com.mohaeng.club.club.application.usecase;

public interface DelegatePresidentUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            Long presidentCandidateParticipantId
    ) {
    }
}
