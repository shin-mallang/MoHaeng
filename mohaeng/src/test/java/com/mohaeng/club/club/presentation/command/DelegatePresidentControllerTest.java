package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.DelegatePresidentUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_DELEGATE_PRESIDENT;
import static com.mohaeng.club.club.presentation.command.DelegatePresidentController.DELEGATE_PRESIDENT_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(DelegatePresidentController.class)
@DisplayName("DelegatePresidentController(회장 위임 컨트롤러) 는")
class DelegatePresidentControllerTest extends ControllerTest {

    @MockBean
    private DelegatePresidentUseCase delegatePresidentUseCase;

    private final DelegatePresidentController.Request request =
            new DelegatePresidentController.Request(1L, 2L);

    @Test
    void 회장_위임에_성공하면_200을_반환한다() throws Exception {
        // given
        doNothing().when(delegatePresidentUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(request)
                .ok();

        then(delegatePresidentUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/delegate-president",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("clubId").type(NUMBER).description("대상 모임의 ID"),
                                fieldWithPath("presidentCandidateParticipantId").type(NUMBER).description("회장으로 위임할 회원의 Participant Id")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .noLogin()
                .jsonContent(request)
                .unAuthorized();

        then(delegatePresidentUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/participant/delegate-president/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_없는_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(delegatePresidentUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(request)
                .notFound();

        then(delegatePresidentUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/delegate-president/fail/Nonexistent Club",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청한_회원이_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(delegatePresidentUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(request)
                .notFound();

        then(delegatePresidentUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/delegate-president/fail/Nonexistent Participant",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 차기_회장_대상_회원이_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(delegatePresidentUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(request)
                .notFound();

        then(delegatePresidentUseCase).should().command(any());
        resultActions.andDo(
                document("club/participant/delegate-president/fail/president candidate is non joined club",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장이_아닌_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ParticipantException(NO_AUTHORITY_DELEGATE_PRESIDENT))
                .when(delegatePresidentUseCase).command(any());

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(request)
                .forbidden();

        then(delegatePresidentUseCase).should().command(any());

        resultActions.andDo(
                document("club/participant/delegate-president/fail/requester does not president or officer",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장_위임_요청_시_Request_Body_필드가_없는_경우_400을_반환한다() throws Exception {
        // given
        DelegatePresidentController.Request emptyRequest = new DelegatePresidentController.Request(null, null);

        // when & then
        ResultActions resultActions = postRequest()
                .url(DELEGATE_PRESIDENT_URL)
                .login()
                .jsonContent(emptyRequest)
                .badRequest();

        then(delegatePresidentUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("club/participant/delegate-president/fail/request fields contains empty value",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}