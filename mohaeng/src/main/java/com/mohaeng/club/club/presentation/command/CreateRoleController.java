package com.mohaeng.club.club.presentation.command;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.command.CreateClubRoleUseCase;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class CreateRoleController {

    public static final String CREATE_CLUB_ROLE_URL = "/api/club/{clubId}/club-role";

    private final CreateClubRoleUseCase createClubRoleUseCase;

    public CreateRoleController(final CreateClubRoleUseCase createClubRoleUseCase) {
        this.createClubRoleUseCase = createClubRoleUseCase;
    }

    @PostMapping(CREATE_CLUB_ROLE_URL)
    public ResponseEntity<Void> create(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long memberId,
            @Valid @RequestBody Request request
    ) {
        createClubRoleUseCase.command(
                new CreateClubRoleUseCase.Command(
                        memberId,
                        clubId,
                        request.name(),
                        request.category()
                ));
        return ResponseEntity.status(CREATED).build();
    }

    public record Request(
            @NotBlank(message = "역할의 이름은 공백이어서는 안됩니다.")
            String name,

            @NotNull(message = "역할의 종류를 반드시 설정하여야 합니다.")
            ClubRoleCategory category
    ) {
    }
}
