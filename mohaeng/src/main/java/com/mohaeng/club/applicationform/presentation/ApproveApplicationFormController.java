package com.mohaeng.club.applicationform.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.applicationform.application.usecase.ApproveApplicationFormUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 가입 신청 수락
 */
@RestController
public class ApproveApplicationFormController {

    public static final String APPROVE_JOIN_CLUB_URL = "/api/application-form/{applicationFormId}/accept";

    private final ApproveApplicationFormUseCase approveApplicationFormUseCase;

    public ApproveApplicationFormController(final ApproveApplicationFormUseCase approveApplicationFormUseCase) {
        this.approveApplicationFormUseCase = approveApplicationFormUseCase;
    }

    @PostMapping(path = APPROVE_JOIN_CLUB_URL)
    public ResponseEntity<Void> approveJoinClubApplication(
            @PathVariable("applicationFormId") final Long applicationFormId,
            @Auth final Long memberId
    ) {
        approveApplicationFormUseCase.command(
                new ApproveApplicationFormUseCase.Command(applicationFormId, memberId)
        );
        return ResponseEntity.ok().build();
    }
}