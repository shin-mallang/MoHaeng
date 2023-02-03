package com.mohaeng.clubrole.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.clubrole.application.usecase.ChangeClubRoleNameUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeClubRoleNameController {

    public static final String CHANGE_CLUB_ROLE_NAME_URL = "/api/club-role/{clubRoleId}";

    private final ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    public ChangeClubRoleNameController(final ChangeClubRoleNameUseCase changeClubRoleNameUseCase) {
        this.changeClubRoleNameUseCase = changeClubRoleNameUseCase;
    }

    @PostMapping(CHANGE_CLUB_ROLE_NAME_URL)
    public ResponseEntity<Void> changeName(
            @PathVariable(name = "clubRoleId") final Long clubRoleId,
            @Auth final Long memberId,
            @Valid @RequestBody ChangeClubRoleNameController.ChangeClubRoleNameRequest request
    ) {
        changeClubRoleNameUseCase.command(
                new ChangeClubRoleNameUseCase.Command(
                        memberId,
                        clubRoleId,
                        request.roleName()
                ));
        return ResponseEntity.ok().build();
    }

    public record ChangeClubRoleNameRequest(
            @NotBlank(message = "역할의 이름은 공백이어서는 안됩니다.")
            String roleName
    ) {
    }
}
