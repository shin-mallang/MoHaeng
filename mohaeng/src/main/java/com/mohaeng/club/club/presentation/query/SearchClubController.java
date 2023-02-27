package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase;
import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase.Result;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository.ClubSearchCond;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.common.presentation.query.PageResponseAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase.Query;

@RestController
public class SearchClubController {

    public static final String SEARCH_CLUB_URL = "/api/club";

    private final QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase;

    public SearchClubController(final QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase) {
        this.queryAllClubBySearchCondUseCase = queryAllClubBySearchCondUseCase;
    }

    @GetMapping(SEARCH_CLUB_URL)
    public ResponseEntity<CommonResponse<List<ClubResponse>>> query(
            @ModelAttribute final ClubSearchRequest clubSearchRequest,
            @PageableDefault(size = 10) final Pageable pageable
    ) {
        Page<Result> result = queryAllClubBySearchCondUseCase.query(
                clubSearchRequest.toQuery(pageable)
        );
        return ResponseEntity.ok(PageResponseAssembler.assemble(result.map(ClubResponse::from)));
    }

    public record ClubSearchRequest(
            String name
    ) {
        public Query toQuery(final Pageable pageable) {
            return new Query(new ClubSearchCond(name), pageable);
        }
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
