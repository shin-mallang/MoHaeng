package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.CreateClubRoleUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.club.club.presentation.command.CreateRoleController.Request;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.CreateRoleController.CREATE_CLUB_ROLE_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
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
@DisplayName("CreateRoleController(?????? ?????? ?????? ????????????) ???")
@WebMvcTest(CreateRoleController.class)
class CreateRoleControllerTest extends ControllerTest {

    @MockBean
    private CreateClubRoleUseCase createClubRoleUseCase;

    private static final Request request = new Request("?????? ??????!", OFFICER);

    @Test
    void ??????_?????????_????????????_200???_????????????() throws Exception {
        // given
        doNothing().when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .created();

        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/create-club-role",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("????????? ????????? ????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("name(????????? ????????? ??????)"),
                                fieldWithPath("category").type(STRING).description("category(????????? ????????? ????????????[OFFICER, GENERAL])")
                        )
                )
        );
    }

    @Test
    void ????????????_??????_????????????_??????_401???_????????????() throws Exception {
        // given

        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .noLogin()
                .jsonContent(request)
                .unAuthorized();

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void ?????????_??????_??????_404???_????????????() throws Exception {
        // given
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .notFound();

        then(createClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/Nonexistent Club",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void ??????_?????????_?????????_???????????????_?????????_??????????????????_??????_??????_404???_????????????() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .notFound();

        then(createClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/Nonexistent Participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void ????????????_??????_??????_?????????_??????_??????_403???_????????????() throws Exception {
        // given
        BDDMockito.doThrow(new ClubRoleException(NO_AUTHORITY_CREATE_ROLE))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .forbidden();

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/requester does not president or officer",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void ??????_?????????_???????????????_??????_400???_????????????() throws Exception {
        // given
        Request request = new Request("?????? ??????!", PRESIDENT);
        doThrow(new ClubRoleException(CAN_NOT_CREATE_PRESIDENT_ROLE))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .badRequest();

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/when create president role",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
        );
    }

    @Test
    void ??????_?????????_?????????_?????????_??????_??????_????????????_??????_409???_????????????() throws Exception {
        // given
        Request request = new Request("???????????? ?????? ??????", OFFICER);
        doThrow(new ClubRoleException(DUPLICATED_NAME))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(request)
                .conflict();

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/duplicated name",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ????????? ?????? ?????? 400??? ????????????.")
    void fail_test_5() throws Exception {
        // given
        Request emptyRequest = new Request("", null);

        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent(emptyRequest)
                .badRequest();

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/request fields contains empty value",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ???????????? ????????? ????????? ?????? 400??? ????????????.")
    void fail_test_6() throws Exception {
        // when & then
        ResultActions resultActions = postRequest()
                .url(CREATE_CLUB_ROLE_URL, 1L)
                .login()
                .jsonContent("{\"name\": \"name\",\"category\":  \"cateGory\"}")
                .badRequest();

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/clubRole/create-club-role/fail/category enum mapping fail",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}