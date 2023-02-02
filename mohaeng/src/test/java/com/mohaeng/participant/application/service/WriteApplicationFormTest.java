package com.mohaeng.participant.application.service;

import com.mohaeng.applicationform.application.usecase.WriteApplicationFormUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.NotificationEventHandlerTestTemplate;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.fixtures.ClubRoleFixture;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.participant.domain.event.ApplicationFormWrittenEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_REQUEST_JOIN_CLUB;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.requestJoinClubUseCaseCommand;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMaxParticipantCount;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("WriteApplicationForm 은 ")
class WriteApplicationFormTest {

    @Autowired
    private WriteApplicationFormUseCase writeApplicationFormUseCase;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("모임에 가입되지 않은 사람많이 가입 신청을 할 수 있다.")
        void success_test_1() {
            // given
            Club club = clubRepository.save(club(null));
            Member member = memberRepository.save(member(null));

            // when
            Long applicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // then
            assertThat(applicationFormId).isNotNull();
        }

        @Test
        @DisplayName("가입이 거절되었더라도 다시 신청할 수 있다.")
        void success_test_2() {
            // given
            Club club = clubRepository.save(club(null));
            Member member = memberRepository.save(member(null));
            Long applicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // 가입 처리
            ApplicationForm applicationForm = applicationFormRepository.findById(applicationFormId).orElse(null);
            ReflectionTestUtils.setField(applicationForm, "processed", true);

            // when
            Long reApplicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // then
            assertThat(reApplicationFormId).isNotNull();
        }

        @Test
        @DisplayName("모임에 이미 사람이 가득 찬 경우에도 가입 신청을 보낼 수 있다.(이는 모임의 최대 인원을 늘리고 가입을 수락할 수 있게 하기 위함이다.)")
        void success_test_3() {
            // given
            Club club = clubRepository.save(clubWithMaxParticipantCount(1));
            club.participantCountUp();  // 모임 참가자 가득 채우기

            Member member = memberRepository.save(member(null));
            Long applicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // 가입 처리
            ApplicationForm applicationForm = applicationFormRepository.findById(applicationFormId).orElse(null);
            ReflectionTestUtils.setField(applicationForm, "processed", true);

            // when
            Long reApplicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // then
            assertThat(reApplicationFormId).isNotNull();
        }

        @Test
        @DisplayName("가입 신청을 하게되면 가입 신청 이벤트가 발행된다.")
        void success_test_4() {
            // given
            Club club = clubRepository.save(club(null));
            Member member = memberRepository.save(member(null));

            // when
            Long applicationFormId = writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // then
            Assertions.assertAll(
                    () -> assertThat(applicationFormId).isNotNull(),
                    () -> assertThat(events.stream(ApplicationFormWrittenEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("이미 가입된 사람이 또다시 신청하는 경우 예외가 발생한다.")
        void fail_test_1() {
            // given
            Club club = clubRepository.save(club(null));
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            clubRoleRepository.saveAll(List.of(presidentRole, generalRole));

            Member generalMember = memberRepository.save(member(null));
            Participant president = new Participant(memberRepository.save(member(null)));
            Participant general = new Participant(generalMember);
            participantRepository.save(president);
            participantRepository.save(general);

            president.joinClub(club, presidentRole);
            general.joinClub(club, generalRole);

            // when & then
            assertThat(assertThrows(ApplicationFormException.class,
                    () -> writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(generalMember.id(), club.id())))
                    .exceptionType())
                    .isEqualTo(ALREADY_MEMBER_JOINED_CLUB);
        }

        @Test
        @DisplayName("이미 신청하였고, 아직 처리되지 않은 경우 다시 신청할 수 없다.")
        void fail_test_2() {
            // given
            Club club = clubRepository.save(club(null));
            Member member = memberRepository.save(member(null));
            writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

            // when & then
            assertThat(assertThrows(ApplicationFormException.class,
                    () -> writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id())))
                    .exceptionType())
                    .isEqualTo(ALREADY_REQUEST_JOIN_CLUB);
        }
    }

    @Disabled("단독으로만 테스트할 것 (함께 테스트하면 롤백이 안되어서 오류남)")
    @Nested
    @DisplayName("가입 신청을 하게되면 `회장`과 `관리자` 에게 알림이 전송된다.")
    public class RequestJoinClubWithEventHandlerTest extends NotificationEventHandlerTestTemplate {

        @Autowired
        private NotificationRepository notificationRepository;

        @Override
        protected void givenAndWhen() {
            Club club = clubRepository.save(club(null));
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);
            ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            clubRoleRepository.saveAll(List.of(presidentRole, officerRole, generalRole));

            Member generalMember = memberRepository.save(member(null));
            Participant president = new Participant(memberRepository.save(member(null)));
            Participant officer = new Participant(memberRepository.save(member(null)));
            participantRepository.save(president);
            participantRepository.save(officer);

            president.joinClub(club, presidentRole);
            officer.joinClub(club, officerRole);

            writeApplicationFormUseCase.command(requestJoinClubUseCaseCommand(generalMember.id(), club.id()));
        }

        @Override
        protected void then() {
            List<Notification> all = notificationRepository.findAll();
            assertThat(all.size()).isEqualTo(2);  // 회원 1 + 임원 1
        }
    }
}