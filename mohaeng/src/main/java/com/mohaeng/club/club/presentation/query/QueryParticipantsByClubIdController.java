package com.mohaeng.club.club.presentation.query;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.common.presentation.query.CommonResponse;
import com.mohaeng.common.presentation.query.PageResponseAssembler;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase.Query;
import static com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase.Result;

@RestController
public class QueryParticipantsByClubIdController {

    public static final String QUERY_PARTICIPANTS_BY_CLUB_ID_URL = "/api/club/{clubId}/participants";

    private final QueryParticipantsByClubIdUseCase queryParticipantsByClubIdUseCase;

    public QueryParticipantsByClubIdController(final QueryParticipantsByClubIdUseCase queryParticipantsByClubIdUseCase) {
        this.queryParticipantsByClubIdUseCase = queryParticipantsByClubIdUseCase;
    }

    @GetMapping(QUERY_PARTICIPANTS_BY_CLUB_ID_URL)
    public ResponseEntity<CommonResponse<List<ParticipantResponse>>> query(
            @Auth final Long memberId,
            @PathVariable(name = "clubId") final Long clubId
    ) {
        final Page<Result> results = queryParticipantsByClubIdUseCase.query(
                new Query(memberId, clubId)
        );
        return ResponseEntity.ok(PageResponseAssembler.assemble(results.map(ParticipantResponse::from)));
    }

    public record ParticipantResponse(
            Long participantId,
            Long clubRoleId,
            String clubRoleName,
            ClubRoleCategory clubRoleCategory,
            Long memberId,
            String memberName,
            LocalDateTime participationDate
    ) {
        public static ParticipantResponse from(final Result result) {
            return new ParticipantResponse(
                    result.participantId(),
                    result.clubRoleId(),
                    result.clubRoleName(),
                    result.clubRoleCategory(),
                    result.memberId(),
                    result.memberName(),
                    result.participationDate()
            );
        }
    }
}
