package com.mohaeng.participant.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.participant.application.usecase.ChangeTargetClubRoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 다른 참여자의 역할을 변경
 */
@RestController
public class ChangeTargetClubRoleController {

    public static final String CHANGE_TARGET_CLUB_ROLE_URL = "/api/participant/{participantId}/change-club-role/{clubRoleId}";

    private final ChangeTargetClubRoleUseCase changeTargetClubRoleUseCase;

    public ChangeTargetClubRoleController(final ChangeTargetClubRoleUseCase changeTargetClubRoleUseCase) {
        this.changeTargetClubRoleUseCase = changeTargetClubRoleUseCase;
    }

    @PostMapping(CHANGE_TARGET_CLUB_ROLE_URL)
    public ResponseEntity<Void> changeTargetRole(
            @PathVariable("participantId") final Long participantId,
            @PathVariable("clubRoleId") final Long clubRoleId,
            @Auth final Long memberId
    ) {
        changeTargetClubRoleUseCase.command(
                new ChangeTargetClubRoleUseCase.Command(
                        memberId,
                        participantId,
                        clubRoleId
                )
        );
        return ResponseEntity.ok().build();
    }
}
