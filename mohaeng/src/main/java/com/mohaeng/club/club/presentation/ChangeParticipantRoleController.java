package com.mohaeng.club.club.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.ChangeParticipantRoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeParticipantRoleController {

    public static final String CHANGE_PARTICIPANT_ROLE_URL = "/api/club/{clubId}/participant/{participantId}/change-role/{clubRoleId}";

    private final ChangeParticipantRoleUseCase changeParticipantRoleUseCase;

    public ChangeParticipantRoleController(final ChangeParticipantRoleUseCase changeParticipantRoleUseCase) {
        this.changeParticipantRoleUseCase = changeParticipantRoleUseCase;
    }

    @PostMapping(CHANGE_PARTICIPANT_ROLE_URL)
    public ResponseEntity<Void> changeParticipantRole(
            @PathVariable("clubId") final Long clubId,
            @PathVariable("participantId") final Long participantId,
            @PathVariable("clubRoleId") final Long clubRoleId,
            @Auth final Long memberId
    ) {
        changeParticipantRoleUseCase.command(
                new ChangeParticipantRoleUseCase.Command(
                        memberId,
                        clubId,
                        participantId,
                        clubRoleId
                )
        );
        return ResponseEntity.ok().build();
    }
}
