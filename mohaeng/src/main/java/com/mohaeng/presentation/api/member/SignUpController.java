package com.mohaeng.presentation.api.member;

import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.domain.member.domain.enums.Gender;
import com.mohaeng.presentation.api.member.mapper.MemberControllerMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "/api")
public class SignUpController {

    public static final String SIGN_UP_URL = "/sign-up";

    private final SignUpUseCase signUpUseCase;

    public SignUpController(final SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    /**
     * 회원 가입을 진행한다.
     */
    @PostMapping(path = SIGN_UP_URL)
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody final SignUpRequest signUpRequest
    ) {
        signUpUseCase.command(
                MemberControllerMapper.toApplicationDto(signUpRequest)
        );

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 회원가입 요청 Request
     */
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
