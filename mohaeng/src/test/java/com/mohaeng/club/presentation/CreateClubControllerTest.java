package com.mohaeng.club.presentation;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.presentation.CreateClubController.CREATE_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static com.mohaeng.common.fixtures.ClubFixture.createClubRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CreateClubController 는 ")
@WebMvcTest(CreateClubController.class)
class CreateClubControllerTest extends ControllerTest {

    @MockBean
    private CreateClubUseCase createClubUseCase;

    private final CreateClubController.CreateClubRequest correctRequest = createClubRequest("name", "dex", 10);

    private final CreateClubController.CreateClubRequest emptyFieldRequest = createClubRequest(" ", "dex", 10);

    private final CreateClubController.CreateClubRequest zeroMaxPeopleCountRequest = createClubRequest("name", "dex", 0);

    private final CreateClubController.CreateClubRequest negativeMaxPeopleCountRequest = createClubRequest("name", "dex", -1);

    @Test
    @DisplayName("인증된 사용자의 올바른 모임 생성 요청인 경우 모임을 생성하고 201을 반환한다.")
    void successTest() throws Exception {
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
                .andDo(print())
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
    @DisplayName("인원을 0으로 설정한 경우 최대 인원으로 설정된다.")
    void whenMaxPeopleCountIs0ThenSettingMax() throws Exception {
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
                .andDo(print())
                .andExpect(status().isCreated());

        verify(createClubUseCase, times(1)).command(any());

        resultActions.andDo(
                document("create-club(max people count is 0 then setting MAX)",
                        getDocumentRequest()
                )
        );
    }

    @Test
    @DisplayName("모임 생성 시 필드가 없는 경우 400을 반환한다.")
    void createClubFailCauseByEmptyRequestFieldWillReturn400() throws Exception {
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
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(createClubUseCase, times(0)).command(any());

        resultActions.andDo(
                document("create-club fail(request fields contains empty value)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("모임 생성 시 회원 수가 음수인 경우 400을 반환한다.")
    void createClubFailCauseByNegativePeopleCountWillReturn400() throws Exception {
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
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(createClubUseCase, times(0)).command(any());

        resultActions.andDo(
                document("create-club fail(max people count is negative)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 경우 401을 반환한다.")
    void createClubFailCauseByUnAuthenticatedUserWillReturn401() throws Exception {
        // given
        final Long memberId = 1L;

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        post(CREATE_CLUB_URL)
                                .content(objectMapper.writeValueAsString(correctRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(createClubUseCase, times(0)).command(any());

        resultActions.andDo(
                document("create-club fail(No Access Token)",
                        getDocumentResponse()
                ));
    }
}