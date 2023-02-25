package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase;
import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase.Query;
import com.mohaeng.common.presentation.query.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase.Result;

@RestController
public class QueryClubByIdController {

    public static final String QUERY_CLUB_BY_ID_URL = "/api/club/{clubId}";

    private final QueryClubByIdUseCase queryClubByIdUseCase;

    public QueryClubByIdController(final QueryClubByIdUseCase queryClubByIdUseCase) {
        this.queryClubByIdUseCase = queryClubByIdUseCase;
    }

    @GetMapping(QUERY_CLUB_BY_ID_URL)
    public ResponseEntity<CommonResponse<ClubResponse>> query(
            @PathVariable(name = "clubId") final Long clubId
    ) {
        Result result = queryClubByIdUseCase.query(
                new Query(clubId)
        );
        return ResponseEntity.ok(CommonResponse.from(ClubResponse.from(result)));
    }

    public record ClubResponse(
            Long id,
            String name,  // 모임의 이름
            String description,  // 모임의 설명
            int maxParticipantCount,  // 최대 참여자 수
            int currentParticipantCount,  // 현재 가입한 회원 수
            LocalDateTime createdAt
    ) {
        public static ClubResponse from(final Result result) {
            return new ClubResponse(
                    result.id(),
                    result.name(),
                    result.description(),
                    result.maxParticipantCount(),
                    result.currentParticipantCount(),
                    result.createdAt()
            );
        }
    }
}
