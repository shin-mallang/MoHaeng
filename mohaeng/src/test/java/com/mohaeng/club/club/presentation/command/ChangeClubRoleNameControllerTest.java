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
@DisplayName("ChangeClubRoleNameController(????????? ?????? ?????? ?????? ????????????) ???")
@WebMvcTest(ChangeClubRoleNameController.class)
class ChangeClubRoleNameControllerTest extends ControllerTest {

    @MockBean
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    private static final Request request = new Request("????????? ?????? ??????");

    final Long clubId = 1L;
    final Long clubRoleId = 2L;

    @Test
    void ?????????_??????_??????_????????????_??????_??????_??????_?????????_??????_??????_??????_?????????_????????????_200???_????????????() throws Exception {
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
                                parameterWithName("clubId").description("?????? ID"),
                                parameterWithName("clubRoleId").description("????????? ????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("roleName").type(STRING).description("name(????????? ????????? ??????)")
                        )
                )
        );
    }

    @Test
    @DisplayName("???????????? ?????? ???????????? ?????? 401??? ????????????.")
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
    void ??????_?????????_?????????_???????????????_?????????_??????????????????_??????_??????_404???_????????????() throws Exception {
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
    void ????????????_??????_?????????_??????_403???_????????????() throws Exception {
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
    void ????????????_????????????_??????_??????_??????_?????????_?????????_???????????????_??????_403???_????????????() throws Exception {
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
    void ??????_??????_??????_??????_??????_???_?????????_??????_??????_400???_????????????() throws Exception {
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
    void ?????????_???????????????_?????????_??????_??????_404???_????????????() throws Exception {
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