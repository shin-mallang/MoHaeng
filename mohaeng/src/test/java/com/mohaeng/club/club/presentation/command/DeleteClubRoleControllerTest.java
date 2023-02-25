package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.DeleteClubRoleUseCase;
import com.mohaeng.club.club.exception.ClubException;
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
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.presentation.command.DeleteClubRoleController.DELETE_CLUB_ROLE_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.AuthenticationFixture.BEARER_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("DeleteClubRoleController 은")
@WebMvcTest(DeleteClubRoleController.class)
class DeleteClubRoleControllerTest extends ControllerTest {

    @MockBean
    private DeleteClubRoleUseCase deleteClubRoleUseCase;

    private final Long memberId = 1L;
    private final Long clubId = 1L;
    private final Long clubRoleId = 2L;

    @Test
    void 모임의_회장_혹은_임원진이_역할_제거_요청을_보낸_경우_역할을_제거하고_200을_반환한다() throws Exception {
        // given
        setAuthentication(memberId);

        doNothing().when(deleteClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andExpect(status().isOk());

        then(deleteClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("delete-club-role",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("모임 ID"),
                                parameterWithName("clubRoleId").description("제거될 역할 ID")
                        )
                )
        );
    }

    @Test
    void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
        // given
        setAuthentication(memberId);

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                )
                .andExpect(status().isUnauthorized());

        then(deleteClubRoleUseCase).shouldHaveNoInteractions();
        resultActions.andDo(
                document("delete-club-role(No Access Token)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_해당_모임에_가입하지_않은_경우_404를_반환한다() throws Exception {
        // given
        setAuthentication(memberId);
        doThrow(new ParticipantException(NOT_FOUND_PARTICIPANT))
                .when(deleteClubRoleUseCase).command(any());

        // when & then
        ResultActions resultActions = mockMvc.perform(
                        delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                                .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
                )
                .andExpect(status().isNotFound());

        verify(deleteClubRoleUseCase, times(1)).command(any());

        resultActions.andDo(
                document("delete-club-role(Nonexistent Participant)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 요청자가_회장_혹은_임원이_아닌_경우_403을_반환환다() throws Exception {
        // given
        setAuthentication(memberId);
        doThrow(new ClubRoleException(NO_AUTHORITY_DELETE_ROLE))
                .when(deleteClubRoleUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isForbidden());

        // when & then
        then(deleteClubRoleUseCase).should().command(any());
        resultActions.andDo(
                document("delete-club-role(requester does not president or officer)",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 기본_역할을_제거하려는_경우_400을_반환한다() throws Exception {
        // given
        setAuthentication(memberId);
        doThrow(new ClubRoleException(CAN_NOT_DELETE_DEFAULT_ROLE))
                .when(deleteClubRoleUseCase).command(any());

        setAuthentication(memberId);

        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isBadRequest());

        // when & then
        verify(deleteClubRoleUseCase, times(1)).command(any());

        resultActions.andDo(
                document("delete-club-role(when delete default role)",
                        getDocumentRequest(),
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_없는_경우_404를_반환한다() throws Exception {
        setAuthentication(memberId);
        doThrow(new ClubException(NOT_FOUND_CLUB))
                .when(deleteClubRoleUseCase).command(any());
        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isNotFound());

        verify(deleteClubRoleUseCase, times(1)).command(any());

        resultActions.andDo(
                document("delete-club-role(Nonexistent Club)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void 제거하려는_역할이_없는_경우_404를_반환한다() throws Exception {
        setAuthentication(memberId);
        doThrow(new ClubRoleException(NOT_FOUND_ROLE))
                .when(deleteClubRoleUseCase).command(any());
        ResultActions resultActions = mockMvc.perform(
                delete(DELETE_CLUB_ROLE_URL, clubId, clubRoleId)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_ACCESS_TOKEN)
        ).andExpect(status().isNotFound());

        verify(deleteClubRoleUseCase, times(1)).command(any());

        resultActions.andDo(
                document("delete-club-role(Nonexistent ClubRole)",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}