package com.mohaeng.notification.presentation;

import com.mohaeng.common.fixtures.NotificationFixture;
import com.mohaeng.common.presentation.ControllerTest;
import com.mohaeng.notification.application.usecase.query.QueryNotificationByIdUseCase;
import com.mohaeng.notification.domain.model.type.*;
import com.mohaeng.notification.exception.NotificationException;
import org.junit.jupiter.api.*;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentRequest;
import static com.mohaeng.common.presentation.ApiDocumentUtils.getDocumentResponse;
import static com.mohaeng.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION;
import static com.mohaeng.notification.presentation.QueryNotificationByIdController.QUERY_NOTIFICATION_BY_ID_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QueryNotificationByIdController 은")
@WebMvcTest(QueryNotificationByIdController.class)
class QueryNotificationByIdControllerTest extends ControllerTest {

    @MockBean
    private QueryNotificationByIdUseCase queryNotificationByIdUseCase;

    private final Long notificationId = 1L;

    @Nested
    @DisplayName("알림 종류별 문서화 테스트")
    class NotificationTypeTest {

        @Test
        @DisplayName("가입 신청서가 작성되면 회장과 임원진에게 전송되는 알림")
        void FillOutApplicationFormNotification() throws Exception {
            // given
            FillOutApplicationFormNotification notification = NotificationFixture.fillOutApplicationFormNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/FillOutApplicationFormNotification",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                            parameterWithName("notificationId").description("알림 ID")
                    ),
                    requestHeaders(
                            headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                    ),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: FillOutApplicationFormNotification"),
                            fieldWithPath("data.clubId").type(NUMBER).description("가입 신청이 작성된 대상 모임"),
                            fieldWithPath("data.applicantId").type(NUMBER).description("가입 신청서를 작성한 회원의 ID"),
                            fieldWithPath("data.applicationFormId").type(NUMBER).description("작성된 가입 신청서의 ID"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("가입 신청서가 거절/수락 된 경우 신청자에게 전송되는 알림")
        void ApplicationProcessedNotification() throws Exception {
            // given
            ApplicationProcessedNotification notification = NotificationFixture.applicationProcessedNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/ApplicationProcessedNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: ApplicationProcessedNotification"),
                            fieldWithPath("data.clubId").type(NUMBER).description("가입 신청이 수락된 대상 모임"),
                            fieldWithPath("data.approved").type(BOOLEAN).description("가입 신청 수락 여부"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("임원진이 가입 신청서를 수락한 경우 회장에게 전송되는 알림")
        void OfficerApproveApplicationNotification() throws Exception {
            // given
            OfficerApproveApplicationNotification notification = NotificationFixture.officerApproveApplicationNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/OfficerApproveApplicationNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: OfficerApproveApplicationNotification"),
                            fieldWithPath("data.officerMemberId").type(NUMBER).description("가입 신청을 수락한 임원진의 Member ID"),
                            fieldWithPath("data.officerParticipantId").type(NUMBER).description("가입 신청을 수락한 임원진의 Participant ID"),
                            fieldWithPath("data.applicantMemberId").type(NUMBER).description("가입한 회원의 Member ID"),
                            fieldWithPath("data.applicantParticipantId").type(NUMBER).description("가입한 회원의 Participant ID"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("임원진이 가입 신청서를 거절한 경우 회장에게 전송되는 알림")
        void OfficerRejectApplicationNotification() throws Exception {
            // given
            OfficerRejectApplicationNotification notification = NotificationFixture.officerRejectApplicationNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/OfficerRejectApplicationNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: OfficerRejectApplicationNotification"),
                            fieldWithPath("data.officerMemberId").type(NUMBER).description("가입 신청을 거절한 임원진의 Member ID"),
                            fieldWithPath("data.officerParticipantId").type(NUMBER).description("가입 신청을 거절한 임원진의 Participant ID"),
                            fieldWithPath("data.applicantMemberId").type(NUMBER).description("거절된 회원의 Member ID"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("모임이 삭제됨으로 인해 가입 신청서가 제거된 경우 신청자에게 전송되는 알림")
        void DeleteApplicationFormCauseByClubDeletedNotification() throws Exception {
            // given
            DeleteApplicationFormCauseByClubDeletedNotification notification = NotificationFixture.deleteApplicationFormCauseByClubDeletedNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/DeleteApplicationFormCauseByClubDeletedNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: DeleteApplicationFormCauseByClubDeletedNotification"),
                            fieldWithPath("data.clubName").type(STRING).description("제거된 모임의 이름"),
                            fieldWithPath("data.clubDescription").type(STRING).description("제거된 모임의 설명"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("모임이 삭제됨으로 인해 모임에서 탈퇴된 경우 기존 모임 참여자에게 전송되는 알림")
        void DeleteParticipantCauseByClubDeletedNotification() throws Exception {
            // given
            DeleteParticipantCauseByClubDeletedNotification notification = NotificationFixture.deleteParticipantCauseByClubDeletedNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/DeleteParticipantCauseByClubDeletedNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: DeleteParticipantCauseByClubDeletedNotification"),
                            fieldWithPath("data.clubName").type(STRING).description("제거된 모임의 이름"),
                            fieldWithPath("data.clubDescription").type(STRING).description("제거된 모임의 설명"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("모임에서 추방된 경우 추방된 사람에게 알리기 위한 알림")
        void ExpelParticipantNotification() throws Exception {
            // given
            ExpelParticipantNotification notification = NotificationFixture.expelParticipantNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/ExpelParticipantNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: ExpelParticipantNotification"),
                            fieldWithPath("data.clubId").type(NUMBER).description("추방된 모임의 ID"),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }

        @Test
        @DisplayName("참여자의 역할이 변경된 경우 해당 참여자에게 전송되는 알림")
        void ParticipantClubRoleChangedNotification() throws Exception {
            // given
            ParticipantClubRoleChangedNotification notification = NotificationFixture.participantClubRoleChangedNotification(notificationId);
            notification.read();
            BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                    .willReturn(notification.toDto());

            // when
            ResultActions resultActions = getRequest()
                    .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                    .login()
                    .noContent()
                    .ok();

            // then
            resultActions.andDo(document("notification/query/query by id/ParticipantClubRoleChangedNotification",
                    getDocumentResponse(),
                    relaxedResponseFields(
                            fieldWithPath("data.id").type(NUMBER).description("알림의 ID"),
                            fieldWithPath("data.createdAt").type(DATE_TIME).description("알림 생성일"),
                            fieldWithPath("data.type").type(STRING).description("알림의 종류: ParticipantClubRoleChangedNotification"),
                            fieldWithPath("data.clubId").type(NUMBER).description("대상 모임의 ID"),
                            fieldWithPath("data.clubRoleId").type(NUMBER).description("대상 역할의 ID"),
                            fieldWithPath("data.clubRoleName").type(STRING).description("변경된 역할의 이름"),
                            fieldWithPath("data.clubRoleCategory").type(STRING).description("변경된 역할의 카테고리 (%s 또는 %s)".formatted(GENERAL.name(), OFFICER.name())),
                            fieldWithPath("data.read").type(BOOLEAN).description("읽었는지 여부(단일 조회 시 항상 true)")
                    )
            ));
        }
    }

    @Test
    void 알림_id_가_없거나_본인이_받은_알림이_아닌_경우_경우_404를_반환한다() throws Exception {
        // given
        BDDMockito.given(queryNotificationByIdUseCase.query(any()))
                .willThrow(new NotificationException(NOT_FOUND_NOTIFICATION));

        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                .login()
                .noContent()
                .notFound();

        resultActions.andDo(
                document("notification/query/query by id/fail/not found notification",
                        getDocumentResponse()
                )
        );
    }

    @Test
    void 인증되지_않은_경우_403을_반환한다() throws Exception {
        // when
        ResultActions resultActions = getRequest()
                .url(QUERY_NOTIFICATION_BY_ID_URL, notificationId)
                .noLogin()
                .noContent()
                .unAuthorized();

        resultActions.andDo(
                document("notification/query/query by id/fail/no access token",
                        getDocumentResponse()
                )
        );
    }
}