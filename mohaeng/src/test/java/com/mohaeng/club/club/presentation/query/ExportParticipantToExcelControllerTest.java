package com.mohaeng.club.club.presentation.query;

import com.mohaeng.club.club.application.usecase.query.ExportParticipantsToExcelUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.presentation.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL;
import static com.mohaeng.club.club.presentation.query.ExportParticipantToExcelController.EXPORT_PARTICIPANT_TO_EXCEL_URL;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ExportParticipantToExcelController(모임의 참여자를 엑셀로 내보내는 컨트롤러) 는")
@WebMvcTest(ExportParticipantToExcelController.class)
class ExportParticipantToExcelControllerTest extends ControllerTest {

    @MockBean
    private ExportParticipantsToExcelUseCase exportParticipantsToExcelUseCase;

    @Test
    void 모임에_존재하는_참여자_명단을_엑셀로_반환한다() throws Exception {
        // when
        final ResultActions resultActions = getRequest()
                .url(EXPORT_PARTICIPANT_TO_EXCEL_URL, 1L)
                .login()
                .noContent()
                .ok();

        // then
        then(exportParticipantsToExcelUseCase).should().export(any());
        resultActions.andDo(document("club/participant/query/export-excel",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                        parameterWithName("clubId").description("클럽 ID")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                ),
                responseHeaders(
                        headerWithName("Content-Disposition")
                                .description("첨부 파일 이름")
                                .optional(),
                        headerWithName("Content-Length")
                                .description("첨부 파일 크기")
                                .optional(),
                        headerWithName("Content-Type")
                                .description("첨부 파일 타입")
                )
        ));
    }

    @Test
    void 인증되지_않은_경우_401() throws Exception {
        // when
        final ResultActions resultActions = getRequest()
                .url(EXPORT_PARTICIPANT_TO_EXCEL_URL, 1L)
                .noLogin()
                .noContent()
                .unAuthorized();

        // then
        resultActions.andDo(
                document("club/participant/query/export-excel/fail/No Access Token",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 모임이_없는_경우_404() throws Exception {
        // given
        BDDMockito.willThrow(new ClubException(NOT_FOUND_CLUB)).given(exportParticipantsToExcelUseCase).export(any());

        // when
        final ResultActions resultActions = getRequest()
                .url(EXPORT_PARTICIPANT_TO_EXCEL_URL, 1L)
                .login()
                .noContent()
                .notFound();

        // then
        resultActions.andDo(
                document("club/participant/query/export-excel/fail/Not Found Club",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 회장이나_임원이_아닌_경우_403() throws Exception {
        // given
        BDDMockito.willThrow(new ParticipantException(NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL)).given(exportParticipantsToExcelUseCase).export(any());

        // when
        final ResultActions resultActions = getRequest()
                .url(EXPORT_PARTICIPANT_TO_EXCEL_URL, 1L)
                .login()
                .noContent()
                .forbidden();

        // then
        resultActions.andDo(
                document("club/participant/query/export-excel/fail/No Authority",
                        getDocumentResponse()
                )
        );
    }
}