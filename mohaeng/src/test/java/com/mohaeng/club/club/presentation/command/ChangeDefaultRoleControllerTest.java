package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.ChangeDefaultRoleUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_DEFAULT_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.ChangeDefaultRoleController.CHANGE_DEFAULT_ROLE_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeDefaultRoleController(기본 역할 변경 컨트롤러) 는")
@WebMvcTest(ChangeDefaultRoleController.class)
class ChangeDefaultRoleControllerTest extends ControllerTest {

    @MockBean
    private ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

    private final Long clubId = 1L;
    private final Long clubRoleId = 2L;

    @Test
    void 모임의_회장_혹은_임원진이_기본_역할을_변경한_경우_200을_반환한다() throws Exception {
        // given
        doNothing().when(changeDefaultRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .login()
                .noContent()
                .expectStatus(HttpStatus.OK);

        then(changeDefaultRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-default-club-role",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("모임 ID"),
                                parameterWithName("clubRoleId").description("기본 역할로 설정할 모임 역할의 ID")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .noLogin()
                .noContent()
                .expectStatus(HttpStatus.UNAUTHORIZED);

        then(changeDefaultRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/change-default-club-role/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 해당_회원이_역할을_뼌경하려는_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(changeDefaultRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .login()
                .noContent()
                .expectStatus(HttpStatus.NOT_FOUND);

        then(changeDefaultRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/clubRole/change-default-club-role/fail/Nonexistent Participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_일반_회원인_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_DEFAULT_ROLE))
                .when(changeDefaultRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .login()
                .noContent()
                .expectStatus(HttpStatus.FORBIDDEN);

        then(changeDefaultRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-default-club-role/fail/requester is general",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_존재하지_않을_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(changeDefaultRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .login()
                .noContent()
                .expectStatus(HttpStatus.NOT_FOUND);

        then(changeDefaultRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-default-club-role/fail/Nonexistent Club",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void 기본_역할로_변경하려는_역할이_존재하지_않을_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ClubRoleException(NOT_FOUND_ROLE))
                .when(changeDefaultRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_DEFAULT_ROLE_URL, clubId, clubRoleId)
                .login()
                .noContent()
                .expectStatus(HttpStatus.NOT_FOUND);

        then(changeDefaultRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-default-club-role/fail/Nonexistent ClubRole",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}