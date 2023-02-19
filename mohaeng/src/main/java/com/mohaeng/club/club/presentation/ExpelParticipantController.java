package com.mohaeng.club.club.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.ExpelParticipantUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpelParticipantController {

    public static final String EXPEL_PARTICIPANT_URL = "/api/club/{clubId}/expel/{participantId}";

    private final ExpelParticipantUseCase expelParticipantUseCase;

    public ExpelParticipantController(final ExpelParticipantUseCase expelParticipantUseCase) {
        this.expelParticipantUseCase = expelParticipantUseCase;
    }

    /**
     * 참여자 제거(모임에서 탈퇴)
     */
    @DeleteMapping(path = EXPEL_PARTICIPANT_URL)
    public ResponseEntity<Void> expel(
            @PathVariable(name = "clubId") final Long clubId,
            @PathVariable(name = "participantId") final Long participantId,
            @Auth final Long memberId
    ) {
        expelParticipantUseCase.command(
                new ExpelParticipantUseCase.Command(memberId, clubId, participantId)
        );
        return ResponseEntity.ok().build();
    }
}
