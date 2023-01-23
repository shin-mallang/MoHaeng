package com.mohaeng.participant.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.participant.application.usecase.LeaveParticipantUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveParticipantController {

    public static final String LEAVE_PARTICIPANT_URL = "/api/participant/{participantId}";

    private final LeaveParticipantUseCase leaveParticipantUseCase;

    public LeaveParticipantController(final LeaveParticipantUseCase leaveParticipantUseCase) {
        this.leaveParticipantUseCase = leaveParticipantUseCase;
    }

    /**
     * 참여자 제거(모임에서 탈퇴)
     */
    @DeleteMapping(path = LEAVE_PARTICIPANT_URL)
    public ResponseEntity<Void> delete(
            @PathVariable(name = "participantId") final Long participantId,
            @Auth final Long memberId
    ) {
        leaveParticipantUseCase.command(
                new LeaveParticipantUseCase.Command(memberId, participantId)
        );
        return ResponseEntity.ok().build();
    }
}
