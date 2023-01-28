package com.mohaeng.club.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.application.usecase.DeleteClubUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteClubController {

    private final DeleteClubUseCase deleteClubUseCase;

    public DeleteClubController(final DeleteClubUseCase deleteClubUseCase) {
        this.deleteClubUseCase = deleteClubUseCase;
    }

    @DeleteMapping("/api/club/{clubId}")
    public ResponseEntity<Void> delete(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long memberId
    ) {
        deleteClubUseCase.command(
                new DeleteClubUseCase.Command(memberId, clubId)
        );
        return ResponseEntity.ok().build();
    }
}
