package com.mohaeng.club.club.presentation;

import com.mohaeng.club.club.application.usecase.CreateClubUseCase;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.presentation.CreateClubController.CREATE_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(CreateClubController.class)
@DisplayName("CreateClubController 은")
class CreateClubControllerTest extends ControllerTest {

    @MockBean
    private CreateClubUseCase createClubUseCase;

    private final CreateClubController.CreateClubRequest correctRequest =
            new CreateClubController.CreateClubRequest("name", "dex", 10);

    private final CreateClubController.CreateClubRequest emptyFieldRequest =
            new CreateClubController.CreateClubRequest("   ", "   ", 10);

    private final CreateClubController.CreateClubRequest zeroMaxPeopleCountRequest =
            new CreateClubController.CreateClubRequest("name", "dex", 0);

    private final CreateClubController.CreateClubRequest negativeMaxPeopleCountRequest =
            new CreateClubController.CreateClubRequest("name", "dex", -1);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        void 인증된_사용자의_올바른_모임_생성_요청인_경우_모임을_생성하고_201을_반환한다() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_URL)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(correctRequest))
                    )
                    .andExpect(status().isCreated());

            verify(createClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(STRING).description("name(모임 이름)"),
                                    fieldWithPath("description").type(STRING).description("description(모임 설명)"),
                                    fieldWithPath("maxParticipantCount").type(NUMBER).description("maxParticipantCount(최대 인원)")
                            )
                    )
            );
        }

        @Test
        void 인원을_0으로_설정한_경우_최대_인원으로_설정된다() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_URL)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(zeroMaxPeopleCountRequest))
                    )
                    .andExpect(status().isCreated());

            verify(createClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("create-club(max people count is 0 then setting MAX)",
                            getDocumentRequest()
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        void 모임_생성_시_필드가_없는_경우_400을_반환한다() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_URL)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(emptyFieldRequest))
                    )
                    .andExpect(status().isBadRequest());

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club fail(request fields contains empty value)",
                            getDocumentResponse()
                    ));
        }

        @Test
        void 모임_생성_시_회원_수가_음수인_경우_400을_반환한다() throws Exception {
            // given
            final Long memberId = 1L;
            setAuthentication(memberId);

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_URL)
                                    .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(negativeMaxPeopleCountRequest))
                    )
                    .andExpect(status().isBadRequest());

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club fail(max people count is negative)",
                            getDocumentResponse()
                    ));
        }

        @Test
        void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
            // given
            final Long memberId = 1L;

            // when & then
            ResultActions resultActions = mockMvc.perform(
                            post(CREATE_CLUB_URL)
                                    .content(objectMapper.writeValueAsString(correctRequest))
                    )
                    .andExpect(status().isUnauthorized());

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("create-club fail(No Access Token)",
                            getDocumentResponse()
                    ));
        }
    }
}