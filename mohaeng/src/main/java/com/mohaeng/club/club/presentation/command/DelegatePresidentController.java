package com.mohaeng.club.club.presentation.command;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.command.DelegatePresidentUseCase;
import com.mohaeng.club.club.application.usecase.command.DelegatePresidentUseCase.Command;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DelegatePresidentController {

    public static final String DELEGATE_PRESIDENT_URL = "/api/club/delegate-president";

    private final DelegatePresidentUseCase delegatePresidentUseCase;

    public DelegatePresidentController(final DelegatePresidentUseCase delegatePresidentUseCase) {
        this.delegatePresidentUseCase = delegatePresidentUseCase;
    }

    @PostMapping(DELEGATE_PRESIDENT_URL)
    public ResponseEntity<Void> delegate(
            @Auth final Long memberId,
            @Valid @RequestBody Request request
    ) {
        delegatePresidentUseCase.command(new Command(
                memberId, request.clubId, request.presidentCandidateParticipantId
        ));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public record Request(
            @NotNull(message = "대상 모임 ID가 없습니다.")
            Long clubId,

            @NotNull(message = "대상 참여자 ID가 없습니다.")
            Long presidentCandidateParticipantId
    ) {
    }
}
