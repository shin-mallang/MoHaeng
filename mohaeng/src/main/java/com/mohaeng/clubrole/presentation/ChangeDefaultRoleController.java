package com.mohaeng.clubrole.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.clubrole.application.usecase.ChangeDefaultRoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임의 기본 역할을 변경하는 컨트롤러
 */
@RestController
public class ChangeDefaultRoleController {

    public static final String CHANGE_DEFAULT_ROLE_URL = "/api/club-role/{clubRoleId}/make-default";

    private final ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

    public ChangeDefaultRoleController(final ChangeDefaultRoleUseCase changeDefaultRoleUseCase) {
        this.changeDefaultRoleUseCase = changeDefaultRoleUseCase;
    }

    @PostMapping(CHANGE_DEFAULT_ROLE_URL)
    public ResponseEntity<Void> changeDefaultRole(
            @PathVariable(name = "clubRoleId") final Long clubRoleId,
            @Auth final Long memberId
    ) {
        changeDefaultRoleUseCase.command(
                new ChangeDefaultRoleUseCase.Command(memberId, clubRoleId)
        );
        return ResponseEntity.ok().build();
    }
}
