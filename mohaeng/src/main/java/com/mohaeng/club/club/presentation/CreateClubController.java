package com.mohaeng.club.club.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.CreateClubUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.status;

@RestController
public class CreateClubController {

    static final String CREATE_CLUB_URL = "/api/club";

    private final CreateClubUseCase createClubUseCase;

    public CreateClubController(final CreateClubUseCase createClubUseCase) {
        this.createClubUseCase = createClubUseCase;
    }

    /**
     * 모임을 생성한다.
     */
    @PostMapping(path = CREATE_CLUB_URL)
    public ResponseEntity<Void> create(
            @Auth final Long memberId,
            @Valid @RequestBody final CreateClubRequest request
    ) {
        createClubUseCase.command(
                new CreateClubUseCase.Command(
                        memberId,
                        request.name(),
                        request.description(),
                        request.maxParticipantCount()));
        return status(CREATED).build();
    }

    public record CreateClubRequest(
            @NotBlank(message = "모임의 이름은 필수입니다.")
            String name,

            @NotBlank(message = "모임의 설명은 필수입니다.")
            String description,

            @Min(0)
            int maxParticipantCount  // 0인 경우 최대로 설정
    ) {
        @Override
        public int maxParticipantCount() {
            if (maxParticipantCount == 0) {
                return Integer.MAX_VALUE;
            }
            return maxParticipantCount;
        }
    }
}
