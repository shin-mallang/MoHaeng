package com.mohaeng.club.club.presentation.command;

import com.mohaeng.club.club.application.usecase.command.CreateClubUseCase;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.presentation.command.CreateClubController.CREATE_CLUB_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(CreateClubController.class)
@DisplayName("CreateClubController(모임 생성 컨트롤러) 는")
class CreateClubControllerTest extends ControllerTest {

    @MockBean
    private CreateClubUseCase createClubUseCase;

    private final CreateClubController.Request correctRequest =
            new CreateClubController.Request("name", "dex", 10);

    private final CreateClubController.Request emptyFieldRequest =
            new CreateClubController.Request("   ", "   ", 10);

    private final CreateClubController.Request zeroMaxPeopleCountRequest =
            new CreateClubController.Request("name", "dex", 0);

    private final CreateClubController.Request negativeMaxPeopleCountRequest =
            new CreateClubController.Request("name", "dex", -1);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        void 인증된_사용자의_올바른_모임_생성_요청인_경우_모임을_생성하고_201을_반환한다() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(CREATE_CLUB_URL)
                    .login()
                    .jsonContent(correctRequest)
                    .expectStatus(HttpStatus.CREATED);

            verify(createClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("club/club/create-club",
                            getDocumentRequest(),
                            getDocumentResponse(),
                            requestHeaders(
                                    headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                            ),
                            requestFields(
                                    fieldWithPath("name").type(STRING).description("name(모임 이름)"),
                                    fieldWithPath("description").type(STRING).description("description(모임 설명)"),
                                    fieldWithPath("maxParticipantCount").type(NUMBER).description("maxParticipantCount(최대 인원)")
                            )
                    )
            );
        }

        @Test
        void 인원을_0으로_설정한_경우_최대_인원으로_설정된다() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(CREATE_CLUB_URL)
                    .login()
                    .jsonContent(zeroMaxPeopleCountRequest)
                    .expectStatus(HttpStatus.CREATED);

            verify(createClubUseCase, times(1)).command(any());

            resultActions.andDo(
                    document("club/club/create-club/option/max people count is 0 then setting MAX",
                            getDocumentRequest()
                    )
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        void 모임_생성_시_필드가_없는_경우_400을_반환한다() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(CREATE_CLUB_URL)
                    .login()
                    .jsonContent(emptyFieldRequest)
                    .expectStatus(HttpStatus.BAD_REQUEST);

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("club/club/create-club/fail/request fields contains empty value",
                            getDocumentResponse()
                    ));
        }

        @Test
        void 모임_생성_시_회원_수가_음수인_경우_400을_반환한다() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(CREATE_CLUB_URL)
                    .login()
                    .jsonContent(negativeMaxPeopleCountRequest)
                    .expectStatus(HttpStatus.BAD_REQUEST);

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("club/club/create-club/fail/max people count is negative",
                            getDocumentResponse()
                    ));
        }

        @Test
        void 인증되지_않은_사용자의_경우_401을_반환한다() throws Exception {
            // when & then
            ResultActions resultActions = postRequest()
                    .url(CREATE_CLUB_URL)
                    .noLogin()
                    .noContent()
                    .expectStatus(HttpStatus.UNAUTHORIZED);

            verify(createClubUseCase, times(0)).command(any());

            resultActions.andDo(
                    document("club/club/create-club/fail/No Access Token",
                            getDocumentResponse()
                    ));
        }
    }
}