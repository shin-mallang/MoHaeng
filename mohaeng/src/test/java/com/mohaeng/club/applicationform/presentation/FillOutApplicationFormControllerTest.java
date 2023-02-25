package com.mohaeng.club.applicationform.presentation;

import com.mohaeng.club.applicationform.application.usecase.FillOutApplicationFormUseCase;
import com.mohaeng.club.applicationform.exception.ApplicationFormException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.club.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static com.mohaeng.club.applicationform.presentation.FillOutApplicationFormController.FILL_OUT_APPLICATION_FORM_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("FillOutApplicationFormController(가입 신청서 작성 컨트롤러) 는")
@WebMvcTest(controllers = FillOutApplicationFormController.class)
class FillOutApplicationFormControllerTest extends ControllerTest {

    @MockBean
    private FillOutApplicationFormUseCase fillOutApplicationFormUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        void 모임_가입_신청_성공_시_200과_신청하였다는_메세지를_반환한다() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(FILL_OUT_APPLICATION_FORM_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andExpect(status().isCreated());

            // when & then
            then(fillOutApplicationFormUseCase).should().command(any());

            resultActions.andDo(
                    document("applicationForm/fill-out-application-form",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            pathParameters(
                                    parameterWithName("clubId").description("가입을 원하는 모임 ID")
                            )
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        void 이미_가입_신청을_보냈으며_해당_요청이_처리되지_않았는데_재요청한_경우_400을_반환한다() throws Exception {
            // given
            when(fillOutApplicationFormUseCase.command(any())).thenThrow(new ApplicationFormException(ALREADY_REQUEST_JOIN_CLUB));
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(FILL_OUT_APPLICATION_FORM_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andExpect(status().isBadRequest());

            // when & then
            then(fillOutApplicationFormUseCase).should().command(any());

            resultActions.andDo(
                    document("applicationForm/fill-out-application-form/fail/already request join club",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        void 이미_모임에_가입한_사람의_경우_400을_반환한다() throws Exception {
            // given
            when(fillOutApplicationFormUseCase.command(any())).thenThrow(new ApplicationFormException(ALREADY_MEMBER_JOINED_CLUB));
            final Long memberId = 1L;
            setAuthentication(memberId);
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(FILL_OUT_APPLICATION_FORM_URL, clubId)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                    )
                    .andExpect(status().isBadRequest());

            // when & then
            then(fillOutApplicationFormUseCase).should().command(any());

            resultActions.andDo(
                    document("applicationForm/fill-out-application-form/fail/member already joined club",
                            getDocumentResponse()
                    )
            );
        }

        @Test
        void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
            // given
            final Long clubId = 1L;
            ResultActions resultActions = mockMvc.perform(
                            post(FILL_OUT_APPLICATION_FORM_URL, clubId)
                    )
                    .andExpect(status().isUnauthorized());

            // when & then
            then(fillOutApplicationFormUseCase).shouldHaveNoInteractions();
            resultActions.andDo(
                    document("applicationForm/fill-out-application-form/fail/No Access Token",
                            getDocumentResponse()
                    )
            );
        }
    }
}