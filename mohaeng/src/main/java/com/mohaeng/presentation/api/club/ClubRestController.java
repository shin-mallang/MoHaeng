package com.mohaeng.presentation.api.club;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.api.authentication.argumentresolver.Auth;
import com.mohaeng.presentation.api.club.mapper.ClubControllerMapper;
import com.mohaeng.presentation.api.club.request.CreateClubRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class ClubRestController {

    public static final String CREATE_CLUB_URL = "/club";

    private final CreateClubUseCase createClubUseCase;

    public ClubRestController(final CreateClubUseCase createClubUseCase) {
        this.createClubUseCase = createClubUseCase;
    }

    @PostMapping(CREATE_CLUB_URL)
    public ResponseEntity<Void> create(
            @Auth Long memberId,
            @Valid @RequestBody final CreateClubRequest createClubRequest
    ) {
        createClubUseCase.command(
                ClubControllerMapper.toApplicationLayerDto(memberId, createClubRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
