package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.RejectJoinClubUseCase;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 가입 신청 거절
 */
@RestController
public class RejectJoinClubController {

    public static final String REJECT_JOIN_CLUB_URL = "/api/application-form/{applicationFormId}/reject";

    private final RejectJoinClubUseCase rejectJoinClubUseCase;

    public RejectJoinClubController(final RejectJoinClubUseCase rejectJoinClubUseCase) {
        this.rejectJoinClubUseCase = rejectJoinClubUseCase;
    }

    @PostMapping(path = REJECT_JOIN_CLUB_URL)
    public ResponseEntity<Void> rejectJoinClubApplication(
            @PathVariable("applicationFormId") final Long applicationFormId,
            @Auth final Long memberId
    ) {
        rejectJoinClubUseCase.command(
                new RejectJoinClubUseCase.Command(applicationFormId, memberId)
        );

        return ResponseEntity.ok().build();
    }
}
