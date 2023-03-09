package com.mohaeng.club.club.presentation.query;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.common.presentation.query.PageResponseAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryParticipatedClubUseCase.Result;

@RestController
public class QueryParticipatedClubController {

    public static final String QUERY_PARTICIPATED_CLUB_URL = "/api/my/club";

    private final QueryParticipatedClubUseCase queryParticipatedClubUseCase;

    public QueryParticipatedClubController(final QueryParticipatedClubUseCase queryParticipatedClubUseCase) {
        this.queryParticipatedClubUseCase = queryParticipatedClubUseCase;
    }

    @GetMapping(QUERY_PARTICIPATED_CLUB_URL)
    public ResponseEntity<CommonResponse<List<ClubResponse>>> query(
            @Auth final Long memberId,
            @PageableDefault(size = 20) final Pageable pageable
    ) {
        final Page<Result> result = queryParticipatedClubUseCase.query(
                new Query(memberId, pageable)
        );
        return ResponseEntity.ok(PageResponseAssembler.assemble(result.map(ClubResponse::from)));
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
