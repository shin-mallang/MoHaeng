package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestJoinClubController {

    public static final String REQUEST_JOIN_CLUB_URL = "/api/club/join/{clubId}";

    private final RequestJoinClubUseCase requestJoinClubUseCase;

    public RequestJoinClubController(final RequestJoinClubUseCase requestJoinClubUseCase) {
        this.requestJoinClubUseCase = requestJoinClubUseCase;
    }

    /**
     * 모임 가입 신청하기
     */
    @PostMapping(path = REQUEST_JOIN_CLUB_URL)
    public ResponseEntity<String> requestJoinClub(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long applicantId
    ) {
        requestJoinClubUseCase.command(
                new RequestJoinClubUseCase.Command(applicantId, clubId)
        );
        return ResponseEntity.ok("가입 신청 요청을 보냈습니다.");
    }
}
