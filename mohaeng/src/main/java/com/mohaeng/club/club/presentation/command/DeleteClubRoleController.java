package com.mohaeng.club.club.presentation.command;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.command.DeleteClubRoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteClubRoleController {

    public static final String DELETE_CLUB_ROLE_URL = "/api/club/{clubId}/club-role/{clubRoleId}";

    private final DeleteClubRoleUseCase deleteClubRoleUseCase;

    public DeleteClubRoleController(final DeleteClubRoleUseCase deleteClubRoleUseCase) {
        this.deleteClubRoleUseCase = deleteClubRoleUseCase;
    }

    @DeleteMapping(DELETE_CLUB_ROLE_URL)
    public ResponseEntity<Void> delete(
            @PathVariable(name = "clubId") final Long clubId,
            @PathVariable(name = "clubRoleId") final Long clubRoleId,
            @Auth final Long memberId
    ) {
        deleteClubRoleUseCase.command(
                new DeleteClubRoleUseCase.Command(
                        memberId, clubId, clubRoleId
                ));
        return ResponseEntity.ok().build();
    }
}
