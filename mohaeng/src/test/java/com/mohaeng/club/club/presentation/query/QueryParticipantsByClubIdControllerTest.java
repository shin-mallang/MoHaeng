package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryParticipantsByClubIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_QUERY_PARTICIPANTS;
import static com.mohaeng.club.club.presentation.query.QueryParticipantsByClubIdController.QUERY_PARTICIPANTS_BY_CLUB_ID_URL;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryParticipantsByClubIdController(????????? ?????? ?????? ????????? ??????) ???")
@WebMvcTest(QueryParticipantsByClubIdController.class)
class QueryParticipantsByClubIdControllerTest extends ControllerTest {

    @MockBean
    private QueryParticipantsByClubIdUseCase queryParticipantsByClubIdUseCase;

    private List<Participant> participants;

    @BeforeEach
    void init() {
        final Club club = club(1L);
        club.registerParticipant(member(2L));
        club.registerParticipant(member(3L));
        club.registerParticipant(member(4L));
        club.registerParticipant(member(5L));
        LongStream.range(0, club.clubRoles().clubRoles().size())
                .forEach(it -> ReflectionTestUtils.setField(club.clubRoles().clubRoles().get((int) it), "id", it));
        LongStream.range(0, club.participants().participants().size())
                .forEach(it -> ReflectionTestUtils.setField(club.participants().participants().get((int) it), "id", it));
        club.participants().participants().forEach(it -> ReflectionTestUtils.setField(it, "createdAt", LocalDateTime.now()));
        participants = club.participants().participants();
    }

    @Test
    void ?????????_??????_??????_????????????_?????????_????????????() throws Exception {
        // given
        BDDMockito.given(queryParticipantsByClubIdUseCase.query(any()))
                .willReturn(new PageImpl<>(participants, PageRequest.of(0, participants.size()), participants.size())
                        .map(QueryParticipantsByClubIdUseCase.Result::from));

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_PARTICIPANTS_BY_CLUB_ID_URL, 1L)
                .login()
                .noContent()
                .ok();

        resultActions.andDo(document("club/participant/query/byClubId",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        pathParameters(
                                parameterWithName("clubId").description("?????? ID")
                        ),
                        responseFields(
                                fieldWithPath("data[].participantId").type(NUMBER).description("???????????? ID"),
                                fieldWithPath("data[].clubRoleId").type(NUMBER).description("???????????? ?????? ID"),
                                fieldWithPath("data[].clubRoleName").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("data[].clubRoleCategory").type(STRING).description("???????????? ?????? ??????(??????, ??????, ??????)"),
                                fieldWithPath("data[].memberId").type(NUMBER).description("???????????? ?????? ID"),
                                fieldWithPath("data[].memberName").type(STRING).description("???????????? ?????? ??????"),
                                fieldWithPath("data[].participationDate").type(DATE_TIME).description("???????????? ????????? ????????? ??????"),

                                fieldWithPath("pageInfo.currentPage").type(NUMBER).description("?????? ????????? ??????, ?????? 1??????"),
                                fieldWithPath("pageInfo.lastPage").type(NUMBER).description("????????? ????????? ??????, ?????? 1??????"),
                                fieldWithPath("pageInfo.pageSize").type(NUMBER).description("??? ???????????? ??????, ???????????? ?????? ????????????"),
                                fieldWithPath("pageInfo.totalElements").type(NUMBER).description("?????? ????????? ????????? ?????? ???????????? ???")
                        )
                )
        );
    }

    @Test
    void ?????????_??????_??????_404???_????????????() throws Exception {
        // given
        BDDMockito.willThrow(new ClubException(NOT_FOUND_CLUB))
                .given(queryParticipantsByClubIdUseCase).query(any());

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_PARTICIPANTS_BY_CLUB_ID_URL, 1L)
                .login()
                .noContent()
                .notFound();

        // then
        resultActions.andDo(document("club/participant/query/byClubId/fail/not found club",
                getDocumentResponse()
        ));
    }

    @Test
    void ?????????_????????????_??????_?????????_?????????_??????_403???_????????????() throws Exception {
        // given
        BDDMockito.willThrow(new ParticipantException(NO_AUTHORITY_QUERY_PARTICIPANTS))
                .given(queryParticipantsByClubIdUseCase).query(any());

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_PARTICIPANTS_BY_CLUB_ID_URL, 1L)
                .login()
                .noContent()
                .forbidden();

        // then
        resultActions.andDo(document("club/participant/query/byClubId/fail/not joined club member request",
                getDocumentResponse()
        ));
    }

    @Test
    void ???????????????_????????????_401???_????????????() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_PARTICIPANTS_BY_CLUB_ID_URL, 1L)
                .noLogin()
                .noContent()
                .unAuthorized();

        // then
        resultActions.andDo(document("club/participant/query/byClubId/fail/no access token",
                getDocumentResponse()
        ));
    }
}