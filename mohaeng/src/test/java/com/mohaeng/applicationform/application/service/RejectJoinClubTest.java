package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.RejectJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.applicationform.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.NotificationEventHandlerTestTemplate;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.kind.OfficerRejectApplicationNotification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED_APPLICATION_FORM;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
import static com.mohaeng.clubrole.domain.model.ClubRole.defaultRoles;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("RejectJoinClub 은 ")
class RejectJoinClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RejectJoinClubUseCase rejectJoinClubUseCase;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ApplicationEvents events;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("가입 신청서를 처리한 후, 회원을 모임에 가입시키지 않는다.")
        void success_test_1() {
            // given
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member managerMember = memberRepository.save(member(null));
            Participant participant = new Participant(managerMember);
            participant.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            participantRepository.save(participant);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            em.flush();
            em.clear();

            // when
            rejectJoinClubUseCase.command(
                    new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
            );

            // then
            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            assertAll(
                    () -> assertThatThrownBy(() -> em.createQuery("select p from Participant p where p.member = :member", Participant.class)
                            .setParameter("member", applicant)
                            .getSingleResult()).isInstanceOf(NoResultException.class),
                    () -> assertThat(findApplicationForm.processed()).isTrue()
            );
        }

        @Test
        @DisplayName("회장이 가입 신청을 처리한 경우 이벤트는 한개만 발행한다.")
        void success_test_2() {
            // given
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member presidentMember = memberRepository.save(member(null));
            Participant president = new Participant(presidentMember);
            president.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            participantRepository.save(president);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            em.flush();
            em.clear();

            // when
            rejectJoinClubUseCase.command(
                    new RejectJoinClubUseCase.Command(applicationForm.id(), presidentMember.id())
            );

            // then
            assertAll(
                    () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerRejectClubJoinApplicationEvent.class).count()).isEqualTo(0L)
            );
        }

        @Test
        @DisplayName("임원진이 가입 신청을 처리한 경우 이벤트는 두개가 발행한다.")
        void success_test_3() {
            // given
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member presidentMember = memberRepository.save(member(null));
            Member officerMember = memberRepository.save(member(null));
            Participant president = new Participant(presidentMember);
            Participant officer = new Participant(officerMember);
            president.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            officer.joinClub(target, clubRoles.get(1));  // 임원으로 가입
            participantRepository.save(president);
            participantRepository.save(officer);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            em.flush();
            em.clear();

            // when
            rejectJoinClubUseCase.command(
                    new RejectJoinClubUseCase.Command(applicationForm.id(), officerMember.id())
            );

            // then
            assertAll(
                    () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(2L),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerRejectClubJoinApplicationEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("관리자가 아닌 경우 회원의 가입 신청을 거절할 수 없다.")
        void fail_test_1() {
            // given
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member managerMember = memberRepository.save(member(null));
            Participant participant = new Participant(managerMember);
            participant.joinClub(target, clubRoles.get(2));  // 일반으로 가입
            participantRepository.save(participant);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            em.flush();
            em.clear();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    rejectJoinClubUseCase.command(
                            new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                    )
            ).exceptionType();

            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM),
                    () -> assertThat(findApplicationForm.processed()).isFalse()
            );
        }

        @Test
        @DisplayName("이미 처리된 신청서의 경우 또다시 처리될 수 없다.")
        void fail_test_2() {
            // given
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member managerMember = memberRepository.save(member(null));
            Participant manager = new Participant(managerMember);
            manager.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            participantRepository.save(manager);

            Member applicantMember = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicantMember.id(), target.id(), null));
            rejectJoinClubUseCase.command(
                    new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
            );
            em.flush();
            em.clear();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    rejectJoinClubUseCase.command(
                            new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                    )
            ).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(ALREADY_PROCESSED_APPLICATION_FORM)
            );
        }
    }

    @Disabled
    @Nested
    @DisplayName("임원이 가입 거절을 하게되면 `신청자`에게는 거절되었다는 알림이, `회장`에게는 임원진이 가입 신청을 처리했다는 알림이 전송된다.")
    public class RejectJoinClubTestWithEventHandlerTest extends NotificationEventHandlerTestTemplate {

        @Autowired
        private NotificationRepository notificationRepository;

        @Override
        protected void givenAndWhen() {
            Club target = clubRepository.save(club(null));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member presidentMember = memberRepository.save(member(null));
            Member officerMember = memberRepository.save(member(null));
            Participant president = new Participant(presidentMember);
            Participant officer = new Participant(officerMember);
            president.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            officer.joinClub(target, clubRoles.get(1));  // 임원으로 가입
            participantRepository.save(president);
            participantRepository.save(officer);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            // when
            rejectJoinClubUseCase.command(
                    new RejectJoinClubUseCase.Command(applicationForm.id(), officerMember.id())
            );
        }

        @Override
        protected void then() {
            List<Notification> all = notificationRepository.findAll();
            Assertions.assertAll(
                    () -> assertThat(all.size()).isEqualTo(2),  // 회원 1 + 임원 1
                    () -> assertThat(all.stream().filter(it -> it.getClass().equals(OfficerRejectApplicationNotification.class)).count()).isEqualTo(1),
                    () -> assertThat(all.stream().filter(it -> it.getClass().equals(ApplicationProcessedNotification.class)).count()).isEqualTo(1)
            );
        }
    }
}