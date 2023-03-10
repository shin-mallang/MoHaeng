package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.IntStream;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME;
import static com.mohaeng.club.club.presentation.query.SearchClubController.SEARCH_CLUB_URL;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("SearchClubController(?????? ??????) ???")
@WebMvcTest(SearchClubController.class)
class SearchClubControllerTest extends ControllerTest {

    @MockBean
    private QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase;

    private Page<QueryAllClubBySearchCondUseCase.Result> club;
    private List<Club> clubs;

    @BeforeEach
    void init() {
        clubs = IntStream.range(1, 5)
                .mapToObj(it -> club((long) it, "clubName " + it, "?????? " + it + "??? ?????? ??????", 100 * it))
                .toList();
    }

    @Test
    void ??????_??????_?????????_?????????_?????????_????????????() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        club = new PageImpl<>(clubs, pageable, clubs.stream().count())
                .map(QueryAllClubBySearchCondUseCase.Result::from);

        // given
        BDDMockito.given(queryAllClubBySearchCondUseCase.query(any()))
                .willReturn(club);

        // when
        ResultActions resultActions = getRequest()
                .url(SEARCH_CLUB_URL + "?name=clubName&page=1&size=10&sort=createdAt,desc")
                .noLogin()
                .noContent()
                .ok();

        resultActions.andDo(document("club/club/query/search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("name").description("?????? ???????????? ????????? ?????? ??????"),
                                parameterWithName("page").description("????????? ?????????"),
                                parameterWithName("size").description("????????? ??????"),
                                parameterWithName("sort").description("????????????")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data[].id").type(NUMBER).description("????????? id"),
                                fieldWithPath("data[].name").type(STRING).description("????????? ??????"),
                                fieldWithPath("data[].description").type(STRING).description("????????? ?????? ??????"),
                                fieldWithPath("data[].maxParticipantCount").type(NUMBER).description("????????? ?????? ?????? ???"),
                                fieldWithPath("data[].currentParticipantCount").type(NUMBER).description("?????? ????????? ???????????? ?????? ???"),
                                fieldWithPath("data[].createdAt").type(DATE_TIME).description("?????????"),

                                fieldWithPath("pageInfo.currentPage").type(NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageInfo.lastPage").type(NUMBER).description("????????? ????????? ??????"),
                                fieldWithPath("pageInfo.pageSize").type(NUMBER).description("??? ???????????? ??????"),
                                fieldWithPath("pageInfo.totalElements").type(NUMBER).description("?????? ????????? ???????????? ?????? ????????? ???")
                        )
                )
        );
    }
}
