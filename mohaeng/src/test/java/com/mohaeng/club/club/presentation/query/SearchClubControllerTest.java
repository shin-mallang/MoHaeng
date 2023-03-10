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
@DisplayName("SearchClubController(모임 검색) 은")
@WebMvcTest(SearchClubController.class)
class SearchClubControllerTest extends ControllerTest {

    @MockBean
    private QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase;

    private Page<QueryAllClubBySearchCondUseCase.Result> club;
    private List<Club> clubs;

    @BeforeEach
    void init() {
        clubs = IntStream.range(1, 5)
                .mapToObj(it -> club((long) it, "clubName " + it, "모임 " + it + "에 대한 설명", 100 * it))
                .toList();
    }

    @Test
    void 모임_조회_성공시_모임의_정보를_반환한다() throws Exception {
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
                                parameterWithName("name").description("검색 조건으로 사용할 모임 이름"),
                                parameterWithName("page").description("조회할 페이지"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬조건")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("data[].id").type(NUMBER).description("모임의 id"),
                                fieldWithPath("data[].name").type(STRING).description("모임의 이름"),
                                fieldWithPath("data[].description").type(STRING).description("모임에 대한 설명"),
                                fieldWithPath("data[].maxParticipantCount").type(NUMBER).description("모임의 최대 인원 수"),
                                fieldWithPath("data[].currentParticipantCount").type(NUMBER).description("현재 모임에 참여중인 인원 수"),
                                fieldWithPath("data[].createdAt").type(DATE_TIME).description("생성일"),

                                fieldWithPath("pageInfo.currentPage").type(NUMBER).description("현제 페이지 번호"),
                                fieldWithPath("pageInfo.lastPage").type(NUMBER).description("마지막 페이지 번호"),
                                fieldWithPath("pageInfo.pageSize").type(NUMBER).description("한 페이지의 크기"),
                                fieldWithPath("pageInfo.totalElements").type(NUMBER).description("해당 조건을 만족하는 전체 모임의 수")
                        )
                )
        );
    }
}
