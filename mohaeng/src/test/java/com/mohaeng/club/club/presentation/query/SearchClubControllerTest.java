package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.QueryAllClubBySearchCondUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.common.ControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.IntStream;

import static com.mohaeng.club.club.presentation.query.SearchClubController.SEARCH_CLUB_URL;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("SearchClubController(모임 검색) 은")
@WebMvcTest(SearchClubController.class)
class SearchClubControllerTest extends ControllerTest {

    @MockBean
    private QueryAllClubBySearchCondUseCase queryAllClubBySearchCondUseCase;

    private Page<QueryAllClubBySearchCondUseCase.Result> club;

    @BeforeEach
    void init() {
        List<Club> clubs = IntStream.range(1, 5)
                .mapToObj(it -> club((long) it, "모임 " + it, "모임 " + it + "에 대한 설명", 100 * it, it))
                .toList();
        Pageable pageable = PageRequest.of(0, 10);
        club = new PageImpl<>(clubs, pageable, clubs.stream().count())
                .map(QueryAllClubBySearchCondUseCase.Result::from);
    }

    @Test
    void 모임_조회_성공시_모임의_정보를_반환한다() throws Exception {
        // given
        BDDMockito.given(queryAllClubBySearchCondUseCase.query(any(), any()))
                .willReturn(club);

        // when
        MvcResult mvcResult = mockMvc.perform(
                get(SEARCH_CLUB_URL)
        ).andDo(document("club/query/search",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        relaxedResponseFields(
                                fieldWithPath("data[].id").type(NUMBER).description("모임의 id"),
                                fieldWithPath("data[].name").type(STRING).description("모임의 이름"),
                                fieldWithPath("data[].description").type(STRING).description("모임에 대한 설명"),
                                fieldWithPath("data[].maxParticipantCount").type(NUMBER).description("모임의 최대 인원 수"),
                                fieldWithPath("data[].currentParticipantCount").type(NUMBER).description("현재 모임에 참여중인 인원 수")
                        )
                )
        ).andReturn();
    }
}