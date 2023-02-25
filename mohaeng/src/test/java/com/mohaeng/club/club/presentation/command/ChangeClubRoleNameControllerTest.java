package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.ChangeClubRoleNameUseCase;
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

import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.ChangeClubRoleNameController.CHANGE_CLUB_ROLE_NAME_URL;
import static com.mohaeng.club.club.presentation.command.ChangeClubRoleNameController.Request;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeClubRoleNameController(모임의 역할 이름 변경 컨트롤러) 는")
@WebMvcTest(ChangeClubRoleNameController.class)
class ChangeClubRoleNameControllerTest extends ControllerTest {

    @MockBean
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    private static final Request request = new Request("변경할 역할 이름");

    final Long clubId = 1L;
    final Long clubRoleId = 2L;

    @Test
    void 모임의_회장_혹은_임원진이_역할_이름_변경_요청을_보낸_경우_역할_이름을_변경하고_200을_반환한다() throws Exception {
        // given
        doNothing().when(changeClubRoleNameUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.OK);

        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-club-role-name",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("모임 ID"),
                                parameterWithName("clubRoleId").description("이름을 변경할 역할의 ID")
                        ),
                        requestFields(
                                fieldWithPath("roleName").type(STRING).description("name(변경할 역할의 이름)")
                        )
                )
        );
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
    void fail_test_1() throws Exception {
        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .noLogin()
                .jsonContent(request)
                .expectStatus(HttpStatus.UNAUTHORIZED);
        then(changeClubRoleNameUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 해당_회원이_역할을_변경하려는_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(changeClubRoleNameUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.NOT_FOUND);

        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/Nonexistent Participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_일반_회원인_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME))
                .when(changeClubRoleNameUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.FORBIDDEN);

        // when & then
        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/requester is general",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_임원이며_임원_혹은_회장_역할의_이름을_변경하려는_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME))
                .when(changeClubRoleNameUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.FORBIDDEN);

        // when & then
        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/requester is officer and try to change officer role name or president role name",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임_역할_이름_변경_요청_시_필드가_없는_경우_400을_반환한다() throws Exception {
        Request request = new Request("");

        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.BAD_REQUEST);

        then(changeClubRoleNameUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/request fields contains empty value",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void 이름을_변경하려는_역할이_없는_경우_404를_반환한다() throws Exception {
        doThrow(new ClubRoleException(NOT_FOUND_ROLE))
                .when(changeClubRoleNameUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                .login()
                .jsonContent(request)
                .expectStatus(HttpStatus.NOT_FOUND);

        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/change-club-role-name/fail/Nonexistent ClubRole",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}