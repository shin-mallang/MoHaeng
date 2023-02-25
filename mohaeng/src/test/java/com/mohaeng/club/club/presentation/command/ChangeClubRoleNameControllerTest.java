package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.ChangeClubRoleNameUseCase;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.ChangeClubRoleNameController.CHANGE_CLUB_ROLE_NAME_URL;
import static com.mohaeng.club.club.presentation.command.ChangeClubRoleNameController.Request;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.then;
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
@DisplayName("ChangeClubRoleNameController(모임의 역할 이름 변경 컨트롤러) 는")
@WebMvcTest(ChangeClubRoleNameController.class)
class ChangeClubRoleNameControllerTest extends ControllerTest {

    @MockBean
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    private static final Request request = new Request("변경할 역할 이름");

    final Long memberId = 1L;
    final Long clubId = 1L;
    final Long clubRoleId = 2L;

    @Test
    void 모임의_회장_혹은_임원진이_역할_이름_변경_요청을_보낸_경우_역할_이름을_변경하고_200을_반환한다() throws Exception {
        // given
        setAuthentication(memberId);
        doNothing().when(changeClubRoleNameUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        then(changeClubRoleNameUseCase).should().command(any());
        resultActions.andDo(
                document("change-club-role-name",
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
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isUnauthorized());

        then(changeClubRoleNameUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("change-club-role-name(No Access Token)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 해당_회원이_역할을_변경하려는_모임에_가입되어있지_않은_경우_404를_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(changeClubRoleNameUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound());

        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("change-club-role-name(Nonexistent Participant)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_일반_회원인_경우_403을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME))
                .when(changeClubRoleNameUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden());

        // when & then
        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("change-club-role-name(requester is general)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_임원이며_임원_혹은_회장_역할의_이름을_변경하려는_경우_403을_반환한다() throws Exception {
        // given
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME))
                .when(changeClubRoleNameUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden());

        // when & then
        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("change-club-role-name(requester is officer and try to change officer role name or president role name)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임_역할_이름_변경_요청_시_필드가_없는_경우_400을_반환한다() throws Exception {
        Request request = new Request("");
        setAuthentication(1L);
        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest());

        then(changeClubRoleNameUseCase).shouldHaveNoInteractions();

        resultActions.andDo(
                document("change-club-role-name(request fields contains empty value)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void 이름을_변경하려는_역할이_없는_경우_404를_반환한다() throws Exception {
        final Long memberId = 1L;
        setAuthentication(memberId);
        doThrow(new ClubRoleException(NOT_FOUND_ROLE))
                .when(changeClubRoleNameUseCase).command(any());
        ResultActions resultActions = mockMvc.perform(
                post(CHANGE_CLUB_ROLE_NAME_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound());

        then(changeClubRoleNameUseCase).should().command(any());

        resultActions.andDo(
                document("change-club-role-name(Nonexistent ClubRole)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}