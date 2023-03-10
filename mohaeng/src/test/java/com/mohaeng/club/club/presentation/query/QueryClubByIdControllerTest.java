package com.mohaeng.club.club.presentation.query;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.presentation.ControllerTest;
import com.mohaeng.common.presentation.query.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.presentation.query.QueryClubByIdController.ClubResponse;
import static com.mohaeng.club.club.presentation.query.QueryClubByIdController.QUERY_CLUB_BY_ID_URL;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryClubByIdController(?????? ?????? ?????? ????????????) ???")
@WebMvcTest(QueryClubByIdController.class)
class QueryClubByIdControllerTest extends ControllerTest {

    @MockBean
    private QueryClubByIdUseCase queryClubByIdUseCase;

    private final Long clubId = 1L;
    private final Club club = club(clubId);

    @Test
    void ??????_??????_?????????_?????????_?????????_????????????() throws Exception {
        // given
        BDDMockito.given(queryClubByIdUseCase.query(any()))
                .willReturn(QueryClubByIdUseCase.Result.from(club));

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_CLUB_BY_ID_URL, clubId)
                .noLogin()
                .noContent()
                .ok();

        MvcResult mvcResult = resultActions.andDo(document("club/club/query/club by id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubId").description("?????? ID")
                        )
                        ,
                        relaxedResponseFields(
                                fieldWithPath("data.id").type(NUMBER).description("????????? id"),
                                fieldWithPath("data.name").type(STRING).description("????????? ??????"),
                                fieldWithPath("data.description").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("data.maxParticipantCount").type(NUMBER).description("????????? ?????? ?????? ???"),
                                fieldWithPath("data.currentParticipantCount").type(NUMBER).description("?????? ????????? ???????????? ?????? ???"),
                                fieldWithPath("data.createdAt").type(DATE_TIME).description("?????????")
                        )
                )
        ).andReturn();

        // then
        CommonResponse<ClubResponse> response
                = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<CommonResponse<ClubResponse>>() {
        });
        assertAll(
                () -> assertThat(response.data().id()).isEqualTo(club.id()),
                () -> assertThat(response.data().name()).isEqualTo(club.name()),
                () -> assertThat(response.data().description()).isEqualTo(club.description()),
                () -> assertThat(response.data().maxParticipantCount()).isEqualTo(club.maxParticipantCount()),
                () -> assertThat(response.data().currentParticipantCount()).isEqualTo(club.currentParticipantCount())
        );
    }

    @Test
    void ??????_id_???_??????_??????_404???_????????????() throws Exception {
        // given
        BDDMockito.given(queryClubByIdUseCase.query(any()))
                .willThrow(new ClubException(NOT_FOUND_CLUB));

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_CLUB_BY_ID_URL, clubId)
                .noLogin()
                .noContent()
                .notFound();

        resultActions.andDo(
                document("club/club/query/club by id/fail/not found club",
                        getDocumentResponse()
                )
        );
    }
}