package com.mohaeng.club.applicationform.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.applicationform.application.usecase.RejectApplicationFormUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모임 가입 신청 거절
 */
@RestController
public class RejectApplicationFormController {

    public static final String REJECT_JOIN_CLUB_URL = "/api/application-form/{applicationFormId}/reject";

    private final RejectApplicationFormUseCase rejectApplicationFormUseCase;

    public RejectApplicationFormController(final RejectApplicationFormUseCase rejectApplicationFormUseCase) {
        this.rejectApplicationFormUseCase = rejectApplicationFormUseCase;
    }

    @PostMapping(path = REJECT_JOIN_CLUB_URL)
    public ResponseEntity<Void> rejectJoinClubApplication(
            @PathVariable("applicationFormId") final Long applicationFormId,
            @Auth final Long memberId
    ) {
        rejectApplicationFormUseCase.command(
                new RejectApplicationFormUseCase.Command(applicationFormId, memberId)
        );
        return ResponseEntity.ok().build();
    }
}