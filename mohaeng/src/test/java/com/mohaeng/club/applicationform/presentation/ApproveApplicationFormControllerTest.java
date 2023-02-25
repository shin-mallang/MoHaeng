package com.mohaeng.club.applicationform.presentation;

import com.mohaeng.club.applicationform.application.usecase.ApproveApplicationFormUseCase;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.*;
import static com.mohaeng.club.applicationform.presentation.ApproveApplicationFormController.APPROVE_JOIN_CLUB_URL;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ApproveApplicationFormController(모임 가입 신청 수락 컨트롤러) 는")
@WebMvcTest(ApproveApplicationFormController.class)
class ApproveApplicationFormControllerTest extends ControllerTest {

    @MockBean
    private ApproveApplicationFormUseCase approveApplicationFormUseCase;

    @Test
    void 모임_가입_신청_수락_성공시_200을_반환한다() throws Exception {
        // given
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .login()
                .expect()
                .ok();

        verify(approveApplicationFormUseCase, times(1)).command(any());

        resultActions.andDo(
                document("applicationForm/approve",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("applicationFormId").description("가입 신청서 ID")
                        )
                )
        );
    }

    @Test
    void 처리자가_임원진이_아닌_경우_403을_반환한다() throws Exception {
        // given
        doThrow(new ApplicationFormException(NO_AUTHORITY_PROCESS_APPLICATION))
                .when(approveApplicationFormUseCase).command(any());
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .login()
                .expect()
                .forbidden();

        verify(approveApplicationFormUseCase, times(1)).command(any());

        resultActions.andDo(
                document("applicationForm/approve/fail/no authority",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 이미_처리된_신청서인_경우_400을_반환한다() throws Exception {
        // given
        doThrow(new ApplicationFormException(ALREADY_PROCESSED_APPLICATION_FORM))
                .when(approveApplicationFormUseCase).command(any());
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .login()
                .expect()
                .badRequest();

        verify(approveApplicationFormUseCase, times(1)).command(any());

        resultActions.andDo(
                document("applicationForm/approve/fail/already processed",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 없는_신청서의_경우_404를_반환한다() throws Exception {
        // given
        doThrow(new ApplicationFormException(NOT_FOUND_APPLICATION_FORM))
                .when(approveApplicationFormUseCase).command(any());
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .login()
                .expect()
                .notFound();

        verify(approveApplicationFormUseCase, times(1)).command(any());

        resultActions.andDo(
                document("applicationForm/approve/fail/no application form",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .noLogin()
                .expect()
                .unAuthorized();

        verify(approveApplicationFormUseCase, times(0)).command(any());

        resultActions.andDo(
                document("applicationForm/approve/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_가득_찬_경우_400을_반환한다() throws Exception {
        // given
        doThrow(new ClubException(CLUB_IS_FULL))
                .when(approveApplicationFormUseCase).command(any());
        final Long applicationFormId = 1L;

        // when & then
        ResultActions resultActions = postRequest()
                .url(APPROVE_JOIN_CLUB_URL, applicationFormId)
                .login()
                .expect()
                .badRequest();

        verify(approveApplicationFormUseCase, times(1)).command(any());

        resultActions.andDo(
                document("applicationForm/approve/fail/club is full",
                        getDocumentResponse()
                )
        );
    }
}