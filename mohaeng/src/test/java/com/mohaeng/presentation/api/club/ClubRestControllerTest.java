package com.mohaeng.presentation.api.club;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.presentation.ControllerTest;
import com.mohaeng.presentation.api.club.request.CreateClubRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.presentation.api.club.ClubRestController.CREATE_CLUB_URL;
import static com.mohaeng.util.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.util.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

@WebMvcTest(controllers = ClubRestController.class)
@DisplayName("ClubRestController 는 ")
class ClubRestControllerTest extends ControllerTest {

    @MockBean
    private CreateClubUseCase createClubUseCase;

    private final CreateClubRequest createClubRequest =
            new CreateClubRequest("sample name", "sample description", 100);

    private final CreateClubRequest emptyRequest =
            new CreateClubRequest("", "", 0);

    @Test
    @DisplayName("모임 생성 성공 시 201을 반환한다.")
    void createClubSuccessThenReturn201() throws Exception {
        setAuthentication(1L);
        ResultActions resultActions = mockMvc.perform(
                        post("/api" + CREATE_CLUB_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createClubRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated());

        resultActions.andDo(document("create-club",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                ),
                requestFields(
                        fieldWithPath("name").type(STRING).description("name(모임 이름)"),
                        fieldWithPath("description").type(STRING).description("description(모임 설명)"),
                        fieldWithPath("maxPeopleCount").type(NUMBER).description("maxPeopleCount(모임 최대 수용 인원)")
                )
        ));
    }

    @Test
    @DisplayName("모임 생성 시 Header에 AccessToken이 없으면 401을 반환한다.")
    void createClubFailCauseByNoAccessTokenThenReturn401() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                        post("/api" + CREATE_CLUB_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createClubRequest))
                )
                .andExpect(status().isUnauthorized());

        resultActions.andDo(
                document("create-club fail(No Access Token)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("모임 생성 시 필드와 AccessToken 모두 없는 경우 경우 401을 반환한다.")
    void createClubFailCauseByNoAccessTokenAndEmptyRequestFieldWillReturn401() throws Exception {
        mockMvc.perform(
                        post("/api" + CREATE_CLUB_URL)
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("모임 생성 시 content body가 없는 경우 400을 반환한다.")
    void createClubFailCauseByNoContentBodyWillReturn400() throws Exception {
        setAuthentication(1L);
        ResultActions resultActions = mockMvc.perform(
                        post("/api" + CREATE_CLUB_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                                .contentType(APPLICATION_JSON)
                                .content("")
                )
                .andExpect(status().isBadRequest());

        resultActions.andDo(
                document("create-club fail(no content body)",
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("모임 생성 시 비어있는 필드가 있는 경우 400을 반환한다.")
    void createClubFailCauseByEmptyRequestFieldWillReturn400() throws Exception {
        setAuthentication(1L);
        ResultActions resultActions = mockMvc.perform(
                        post("/api" + CREATE_CLUB_URL)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emptyRequest))
                )
                .andExpect(status().isBadRequest());

        resultActions.andDo(
                document("create-club fail(request fields contains empty value)",
                        getDocumentResponse()
                ));
    }
}