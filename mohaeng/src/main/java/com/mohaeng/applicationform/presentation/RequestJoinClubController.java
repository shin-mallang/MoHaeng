package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.WriteApplicationFormUseCase;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestJoinClubController {

    public static final String REQUEST_JOIN_CLUB_URL = "/api/club/join/{clubId}";

    private final WriteApplicationFormUseCase writeApplicationFormUseCase;

    public RequestJoinClubController(final WriteApplicationFormUseCase writeApplicationFormUseCase) {
        this.writeApplicationFormUseCase = writeApplicationFormUseCase;
    }

    /**
     * 모임 가입 신청하기
     */
    @PostMapping(path = REQUEST_JOIN_CLUB_URL)
    public ResponseEntity<String> requestJoinClub(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long applicantId
    ) {
        writeApplicationFormUseCase.command(
                new WriteApplicationFormUseCase.Command(applicantId, clubId)
        );
        return ResponseEntity.ok("가입 신청 요청을 보냈습니다.");
    }
}
