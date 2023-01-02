package com.mohaeng.presentation.api.club;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.api.club.mapper.ClubPersistenceMapper;
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
public class CreateClubRestController {

    private final CreateClubUseCase createClubUseCase;

    public CreateClubRestController(final CreateClubUseCase createClubUseCase) {
        this.createClubUseCase = createClubUseCase;
    }

    /**
     * 모임을 생성한다.
     */
    @PostMapping(path = "/api/club")
    public ResponseEntity<Void> create(
            @Valid @RequestBody final CreateClubRequest request
    ) {
        createClubUseCase.command(ClubPersistenceMapper.toApplicationDto(request));
        return status(CREATED).build();
    }


    public record CreateClubRequest(
            @NotBlank(message = "모임의 이름은 필수입니다.")
            String name,
            @NotBlank(message = "모임의 설명은 필수입니다.")
            String description,
            @Min(0)
            int maxPeopleCount  // 0인 경우 최대로 설정
    ) {
        @Override
        public int maxPeopleCount() {
            if (maxPeopleCount == 0) {
                return Integer.MAX_VALUE;
            }
            return maxPeopleCount;
        }
    }
}
