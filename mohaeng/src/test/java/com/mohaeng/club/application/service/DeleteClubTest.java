package com.mohaeng.club.application.service;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.application.usecase.DeleteClubUseCase;
import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.exception.ClubExceptionType.NO_AUTHORITY_DELETE_CLUB;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@ApplicationTest
@DisplayName("DeleteClub 은 ")
class DeleteClubTest {

    @Autowired
    private DeleteClubUseCase deleteClubUseCase;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private ApplicationEvents events;

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("요청자가 모임의 회장인 경우, 모임의 가입자, 모임의 역할, 모임, 가입 신청서를 모두 제거한다.")
        void success_test_1() {
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveApplicationForm(club, true);
            saveApplicationForm(club, false);
            flushAndClear();

            deleteClubUseCase.command(
                    new DeleteClubUseCase.Command(presidentMember.id(), club.id())
            );
            flushAndClear();
            assertAll(
                    () -> assertThat(clubRepository.findById(club.id())).isEmpty(),
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(em.createQuery("select af from ApplicationForm af", ApplicationForm.class)
                            .getResultList().size())
                            .isEqualTo(0)
            );
        }

        @Test
        @DisplayName("모임 제거 시 모임 제거 이벤트가 발행된다.")
        void success_test_2() {
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            Participant officer1 = saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            Participant officer2 = saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            Participant general1 = saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            Participant general2 = saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));

            deleteClubUseCase.command(
                    new DeleteClubUseCase.Command(presidentMember.id(), club.id())
            );
            flushAndClear();
            assertAll(
                    () -> assertThat(clubRepository.findById(club.id())).isEmpty(),
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class)
                            .getResultList().size())
                            .isEqualTo(0),
                    () -> assertThat(events.stream(DeleteClubEvent.class).count()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @ParameterizedTest(name = "요청자가 모임의 회장이 아닌 경우(EX: {arguments}) 예외가 발생한다.")
        @EnumSource(mode = EXCLUDE, names = {"PRESIDENT"})
        void fail_test_1(final ClubRoleCategory clubRoleCategory) {
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(clubRoleCategory));

            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(GENERAL));

            BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () -> deleteClubUseCase.command(
                    new DeleteClubUseCase.Command(member.id(), club.id())
            )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_CLUB),
                    () -> assertThat(clubRepository.findById(club.id())).isNotEmpty()
            );
        }

        @Test
        @DisplayName("모임이 없는 경우 예외가 발생한다.")
        void fail_test_2() {
            Member member = saveMember();
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () -> deleteClubUseCase.command(
                    new DeleteClubUseCase.Command(member.id(), 10L)
            )).exceptionType();

            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB)
            );
        }
    }

    private ApplicationForm saveApplicationForm(final Club club, final boolean processed) {
        ApplicationForm applicationForm = applicationForm(saveMember().id(), club.id(), null);
        ReflectionTestUtils.setField(applicationForm, "processed", processed);
        return applicationFormRepository.save(applicationForm);
    }

    private Participant saveParticipant(final Member member, final Club club, final ClubRole clubRole) {
        return participantRepository.save(participant(null, member, club, clubRole));
    }

    private Map<ClubRoleCategory, ClubRole> saveDefaultClubRoles(final Club club) {
        return clubRoleRepository.saveAll(ClubRole.defaultRoles(club))
                .stream()
                .collect(Collectors.toUnmodifiableMap(ClubRole::clubRoleCategory, it -> it));
    }

    private Club saveClub() {
        return clubRepository.save(club(null));
    }

    private Member saveMember() {
        return memberRepository.save(member(null));
    }
}