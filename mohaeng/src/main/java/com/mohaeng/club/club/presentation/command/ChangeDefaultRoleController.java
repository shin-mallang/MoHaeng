package com.mohaeng.club.club.presentation.command;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.command.ChangeDefaultRoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeDefaultRoleController {

    public static final String CHANGE_DEFAULT_ROLE_URL = "/api/club/{clubId}/club-role/{clubRoleId}/make-default";

    private final ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

    public ChangeDefaultRoleController(final ChangeDefaultRoleUseCase changeDefaultRoleUseCase) {
        this.changeDefaultRoleUseCase = changeDefaultRoleUseCase;
    }

    @PostMapping(CHANGE_DEFAULT_ROLE_URL)
    public ResponseEntity<Void> changeDefaultRole(
            @PathVariable(name = "clubId") final Long clubId,
            @PathVariable(name = "clubRoleId") final Long clubRoleId,
            @Auth final Long memberId
    ) {
        changeDefaultRoleUseCase.command(
                new ChangeDefaultRoleUseCase.Command(memberId, clubId, clubRoleId)
        );
        return ResponseEntity.ok().build();
    }
}
