package com.mohaeng.applicationform.application.eventhandler;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DeleteApplicationFormWithDeleteClubEventHandler 는 ")
class DeleteApplicationFormWithDeleteClubEventHandlerTest extends EventHandlerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    private DeleteApplicationFormWithDeleteClubEventHandler handler;

    @BeforeEach
    public void init() {
        handler = new DeleteApplicationFormWithDeleteClubEventHandler(eventHistoryRepository, applicationFormRepository);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("DeleteClubEvent를 받으면 존재하는 가입 신청서를 모두 제거한다.")
        void success_test_1() {
            // given
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveApplicationForm(club, false);
            saveApplicationForm(club, false);
            saveApplicationForm(club, false);
            flushAndClear();

            // when
            handler.handle(new DeleteClubEvent(this, club.id(), club.name(), club.description()));
            flushAndClear();

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select af from ApplicationForm af", ApplicationForm.class)
                            .getResultList().size())
                            .isEqualTo(0)
            );
        }

        @Test
        @DisplayName("가입 신청서를 모두 제거한 뒤, 가입 신청서 제거 이벤트를 발행한다.")
        void success_test_2() {
            // given
            Member presidentMember = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            saveParticipant(presidentMember, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));
            saveParticipant(saveMember(), club, clubRoleCategoryClubRoleMap.get(OFFICER));
            saveApplicationForm(club, false);
            saveApplicationForm(club, false);
            saveApplicationForm(club, false);
            flushAndClear();

            // when
            handler.handle(new DeleteClubEvent(this, club.id(), club.name(), club.description()));
            flushAndClear();

            // then
            assertAll(
                    () -> assertThat(events.stream(DeleteApplicationFormEvent.class).count()).isEqualTo(1L)
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