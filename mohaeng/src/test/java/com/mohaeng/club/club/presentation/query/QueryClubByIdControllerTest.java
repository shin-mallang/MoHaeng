package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryClubByIdUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.presentation.query.QueryClubByIdController.QUERY_CLUB_BY_ID_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryClubByIdController(모임 단일 조회 컨트롤러) 은")
@WebMvcTest(QueryClubByIdController.class)
class QueryClubByIdControllerTest extends ControllerTest {

    @MockBean
    private QueryClubByIdUseCase queryClubByIdUseCase;

    private final Long clubId = 1L;
    private final Club club = club(clubId);

    @Test
    void 모임_조회_성공시_모임의_정보를_반환한다() throws Exception {
        // given
        BDDMockito.given(queryClubByIdUseCase.query(any()))
                .willReturn(QueryClubByIdUseCase.Result.from(club));

        // when
        MvcResult mvcResult = mockMvc.perform(
                get(QUERY_CLUB_BY_ID_URL, clubId)
        ).andDo(document("query club by id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubId").description("모임 ID")
                        )
                        ,
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("모임의 id"),
                                fieldWithPath("name").type(STRING).description("모임의 이름"),
                                fieldWithPath("description").type(STRING).description("모임에 대한 설명"),
                                fieldWithPath("maxParticipantCount").type(NUMBER).description("모임의 최대 인원 수"),
                                fieldWithPath("currentParticipantCount").type(NUMBER).description("현재 모임에 참여중인 인원 수")
                        )
                )
        ).andReturn();

        // then
        QueryClubByIdController.ClubResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), QueryClubByIdController.ClubResponse.class);
        assertAll(
                () -> assertThat(response.id()).isEqualTo(club.id()),
                () -> assertThat(response.name()).isEqualTo(club.name()),
                () -> assertThat(response.description()).isEqualTo(club.description()),
                () -> assertThat(response.maxParticipantCount()).isEqualTo(club.maxParticipantCount()),
                () -> assertThat(response.currentParticipantCount()).isEqualTo(club.currentParticipantCount())
        );
    }

    @Test
    void 모임_id_가_없는_경우_404를_반환한다() throws Exception {
        // given
        BDDMockito.given(queryClubByIdUseCase.query(any()))
                .willThrow(new ClubException(NOT_FOUND_CLUB));

        // when
        ResultActions resultActions = mockMvc.perform(
                get(QUERY_CLUB_BY_ID_URL, clubId)
        ).andExpect(status().isNotFound());

        resultActions.andDo(
                document("query club by id (not found club)",
                        getDocumentResponse()
                )
        );
    }
}