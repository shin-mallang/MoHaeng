package com.mohaeng.club.applicationform.presentation;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.applicationform.application.usecase.FillOutApplicationFormUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FillOutApplicationFormController {

    public static final String FILL_OUT_APPLICATION_FORM_URL = "/api/club/join/{clubId}";

    private final FillOutApplicationFormUseCase fillOutApplicationFormUseCase;

    public FillOutApplicationFormController(final FillOutApplicationFormUseCase fillOutApplicationFormUseCase) {
        this.fillOutApplicationFormUseCase = fillOutApplicationFormUseCase;
    }

    /**
     * 모임 가입 신청하기
     */
    @PostMapping(path = FILL_OUT_APPLICATION_FORM_URL)
    public ResponseEntity<String> fillOut(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long applicantId
    ) {
        fillOutApplicationFormUseCase.command(
                new FillOutApplicationFormUseCase.Command(applicantId, clubId)
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
