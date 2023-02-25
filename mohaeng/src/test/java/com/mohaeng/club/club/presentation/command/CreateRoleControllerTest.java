package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.CreateClubRoleUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.club.club.presentation.command.CreateRoleController.Request;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.CreateRoleController.CREATE_CLUB_ROLE_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("CreateRoleController(모임 역할 생성 컨트롤러) 는")
@WebMvcTest(CreateRoleController.class)
class CreateRoleControllerTest extends ControllerTest {

    @MockBean
    private CreateClubRoleUseCase createClubRoleUseCase;

    private static final Request request = new Request("역할 이름!", OFFICER);

    @Test
    void 역할_생성에_성공하면_200을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doNothing().when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        post(CREATE_CLUB_ROLE_URL, 1L)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());

        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("create-club-role",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("역할을 생성할 모임의 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(STRING).description("name(생성할 역할의 이름)"),
                                fieldWithPath("category").type(STRING).description("category(생성할 역할의 카테고리[OFFICER, GENERAL])")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized());

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("create-club-role(No Access Token)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_없는_경우_404를_빤환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound());

        then(createClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("create-club-role(Nonexistent Club)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 해당_회원이_역할을_생성하려는_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(createClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound());

        then(createClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("create-club-role(Nonexistent Participant)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장_혹은_임원이_아닌_경우_403을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        BDDMockito.doThrow(new ClubRoleException(NO_AUTHORITY_CREATE_ROLE))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden());

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("create-club-role(requester does not president or officer)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장_역할을_생성하려는_경우_400을_반환한다() throws Exception {
        // given
        Request request = new Request("역할 이름!", PRESIDENT);
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubRoleException(CAN_NOT_CREATE_PRESIDENT_ROLE))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("create-club-role(when create president role)",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 이미_동일한_이름의_역할이_모임_내에_존재하는_경우_409를_반환한다() throws Exception {
        // given
        Request request = new Request("중복되는 역할 이름", OFFICER);
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubRoleException(DUPLICATED_NAME))
                .when(createClubRoleUseCase).command(any());

        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isConflict());

        // when & then
        then(createClubRoleUseCase).should().command(any());

        resultActions.andDo(
                document("create-club-role(duplicated name)",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
        );
    }

    @Test
    @DisplayName("모임 역할 생성 시 필드가 없는 경우 400을 반환한다.")
    void fail_test_5() throws Exception {
        Request emptyRequest = new Request("", null);
        setAuthentication(1L);

        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest))
        ).andExpect(status().isBadRequest());

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("create-club-role(request fields contains empty value)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("모임 역할 생성 시 카테고리 필드가 잘못된 경우 400을 반환한다.")
    void fail_test_6() throws Exception {
        setAuthentication(1L);
        ResultActions resultActions = mockMvc.perform(
                post(CREATE_CLUB_ROLE_URL, 1L)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"name\",\"category\":  \"cateGory\"}")
        ).andExpect(status().isBadRequest());

        then(createClubRoleUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("create-club-role(category enum mapping fail)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}