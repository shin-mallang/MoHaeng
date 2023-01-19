package com.mohaeng.applicationform.presentation;

import com.mohaeng.applicationform.application.usecase.ApproveJoinClubUseCase;
import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 가입 신청 수락
 */
@RestController
public class ApproveJoinClubController {

    public static final String APPROVE_JOIN_CLUB_URL = "/api/application-form/{applicationFormId}/accept";

    private final ApproveJoinClubUseCase approveJoinClubUseCase;

    public ApproveJoinClubController(final ApproveJoinClubUseCase approveJoinClubUseCase) {
        this.approveJoinClubUseCase = approveJoinClubUseCase;
    }

    @PostMapping(path = APPROVE_JOIN_CLUB_URL)
    public ResponseEntity<Void> approveJoinClubApplication(
            @PathVariable("applicationFormId") final Long applicationFormId,
            @Auth final Long memberId
    ) {
        approveJoinClubUseCase.command(
                new ApproveJoinClubUseCase.Command(applicationFormId, memberId)
        );
        return ResponseEntity.ok().build();
    }
}
