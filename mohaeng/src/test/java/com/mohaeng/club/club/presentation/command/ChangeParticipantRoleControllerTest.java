package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.ChangeParticipantRoleUseCase;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
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

import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static com.mohaeng.club.club.presentation.command.ChangeParticipantRoleController.CHANGE_PARTICIPANT_ROLE_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeParticipantRoleController(참여자의 역할 변경 컨트롤러) 는")
@WebMvcTest(ChangeParticipantRoleController.class)
class ChangeParticipantRoleControllerTest extends ControllerTest {

    @MockBean
    private ChangeParticipantRoleUseCase changeParticipantRoleUseCase;

    private final Long clubId = 1L;
    private final Long participantId = 2L;
    private final Long clubRoleId = 3L;

    @Test
    void 대상의_역할_변경_성공_시_200을_반환한다() throws Exception {
        // given
        doNothing().when(changeParticipantRoleUseCase).command(any());

        // when
        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .login()
                .expect()
                .expectStatus(OK);

        // then
        then(changeParticipantRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/change-participant's-role",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("대상 회원의 모임 ID"),
                                parameterWithName("participantId").description("역할을 변경할 참여자의 ID"),
                                parameterWithName("clubRoleId").description("변경하고싶은 역할의 ID")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        doNothing().when(changeParticipantRoleUseCase).command(any());

        // when
        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .noLogin()
                .expect()
                .expectStatus(UNAUTHORIZED);

        // when & then
        BDDMockito.then(changeParticipantRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/participant/change-participant's-role/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 역할을_변경할_대상_참가자의_ID가_없거나_혹은_내가_모임에_가입되지_않는_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(changeParticipantRoleUseCase).command(any());

        // when
        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .login()
                .expect()
                .expectStatus(NOT_FOUND);

        // then
        then(changeParticipantRoleUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/change-participant's-role/fail/Nonexistent Participant ID",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 변경하고자_하는_역할의_ID가_없거나_다른_모임에_속한_역할의_ID일_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ClubRoleException(NOT_FOUND_ROLE))
                .when(changeParticipantRoleUseCase).command(any());

        // when
        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .login()
                .expect()
                .expectStatus(NOT_FOUND);

        // then
        then(changeParticipantRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/change-participant's-role/fail/Nonexistent ClubRole ID",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장이_아닌_회원이_변경을_요청한_경우_403_을_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE))
                .when(changeParticipantRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .login()
                .expect()
                .expectStatus(FORBIDDEN);

        then(changeParticipantRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/change-participant's-role/fail/requester is general participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장으로_변경하려는_경우_400을_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_CHANGE_PRESIDENT_ROLE))
                .when(changeParticipantRoleUseCase).command(any());

        ResultActions resultActions = postRequest()
                .url(CHANGE_PARTICIPANT_ROLE_URL, clubId, participantId, clubRoleId)
                .login()
                .expect()
                .expectStatus(BAD_REQUEST);

        // when & then
        then(changeParticipantRoleUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/change-participant's-role/fail/change to president role",
                        getDocumentResponse()
                )
        );
    }
}