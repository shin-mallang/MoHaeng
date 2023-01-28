package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.ApproveJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.BaseEvent;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.kind.OfficerApproveApplicationNotification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.clubrole.domain.model.ClubRole.defaultRoles;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("ApproveJoinClub 은 ")
class ApproveJoinClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ApproveJoinClubUseCase approveJoinClubUseCase;

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
        @DisplayName("회원을 모임에 기본 역할로 가입시킨다.")
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
            approveJoinClubUseCase.command(
                    new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
            );

            // then
            Participant member = em.createQuery("select p from Participant p where p.member = :member", Participant.class)
                    .setParameter("member", applicant)
                    .getSingleResult();

            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            assertAll(
                    () -> assertThat(member.clubRole().clubRoleCategory()).isEqualTo(ClubRoleCategory.GENERAL),
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
            approveJoinClubUseCase.command(
                    new ApproveJoinClubUseCase.Command(applicationForm.id(), presidentMember.id())
            );

            // then
            assertAll(
                    () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerApproveClubJoinApplicationEvent.class).count()).isEqualTo(0L)
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
            approveJoinClubUseCase.command(
                    new ApproveJoinClubUseCase.Command(applicationForm.id(), officerMember.id())
            );

            // then
            assertAll(
                    () -> assertThat(events.stream(BaseEvent.class).count()).isEqualTo(2L),
                    () -> assertThat(events.stream(ApplicationProcessedEvent.class).count()).isEqualTo(1L),
                    () -> assertThat(events.stream(OfficerApproveClubJoinApplicationEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("관리자가 아닌 경우 회원을 모임에 가입시킬 수 없다.")
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
                    approveJoinClubUseCase.command(
                            new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
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
            approveJoinClubUseCase.command(
                    new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
            );

            em.flush();
            em.clear();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    approveJoinClubUseCase.command(
                            new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                    )
            ).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_APPLICATION_FORM)
            );
        }

        // TODO : 테스트에 존재하는 @Transactional로 묶여서, 롤백이 정상적으로 수행되는지 확인할 수 없다. 어카징.. (일단 코드 순서 재배치를 통해 해결).
        @Test
        @DisplayName("모임이 가득 찬 경우 더이상 회원을 받을 수 없다.")
        void fail_test_3() {
            // given
            Club target = clubRepository.save(new Club("name", "des", 2));
            List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
            Member managerMember = memberRepository.save(member(null));
            target.participantCountUp();
            Participant participant = new Participant(managerMember);
            participant.joinClub(target, clubRoles.get(0));  // 회장으로 가입
            participantRepository.save(participant);

            Member applicant = memberRepository.save(member(null));
            ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

            em.flush();
            em.clear();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                    approveJoinClubUseCase.command(
                            new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                    )
            ).exceptionType();

            // then
            ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
            Club club = clubRepository.findById(target.id()).get();
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(CLUB_IS_FULL),
                    () -> assertThat(findApplicationForm.processed()).isFalse(),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(club.maxParticipantCount())
            );
        }
    }

    @Nested
    @DisplayName("ApproveJoinClub + 이벤트 핸들러 테스트 ")
    public class ApproveJoinClubTestWithEventHandlerTest {

        @Autowired
        private NotificationRepository notificationRepository;

        @Test
        @DisplayName("임원이 가입 승인을 하게되면 `신청자`에게는 승인되었다는 알림이, `회장`에게는 임원진이 가입 신청을 처리했다는 알림이 전송된다.")
        void test() {
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
            approveJoinClubUseCase.command(
                    new ApproveJoinClubUseCase.Command(applicationForm.id(), officerMember.id())
            );

            // then
            List<Notification> all = notificationRepository.findAll();
            Assertions.assertAll(
                    () -> assertThat(all.size()).isEqualTo(2),  // 회원 1 + 임원 1
                    () -> assertThat(all.stream().filter(it -> it.getClass().equals(OfficerApproveApplicationNotification.class)).count()).isEqualTo(1),
                    () -> assertThat(all.stream().filter(it -> it.getClass().equals(ApplicationProcessedNotification.class)).count()).isEqualTo(1)
            );
        }
    }
}