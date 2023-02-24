package com.mohaeng.member.presentation;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.mohaeng.member.application.usecase.SignUpUseCase.Command;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class SignUpController {

    public static final String SIGN_UP_URL = "/api/sign-up";

    private final SignUpUseCase signUpUseCase;

    public SignUpController(final SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    @PostMapping(path = SIGN_UP_URL)
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody final SignUpRequest signUpRequest
    ) {
        signUpUseCase.command(new Command(
                signUpRequest.username(),
                signUpRequest.password(),
                signUpRequest.name(),
                signUpRequest.age(),
                signUpRequest.gender())
        );
        return ResponseEntity.status(CREATED).build();
    }

    public record SignUpRequest(
            @NotBlank(message = "아이디가 입력되지 않았습니다.")
            String username,

            @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
            String password,

            @NotBlank(message = "이름이 입력되지 않았습니다.")
            String name,

            @Min(value = 1, message = "나이는 1살 이상이어야 합니다.")
            int age,

            @NotNull(message = "성별이 입력되지 않았습니다.")
            Gender gender
    ) {
    }
}
