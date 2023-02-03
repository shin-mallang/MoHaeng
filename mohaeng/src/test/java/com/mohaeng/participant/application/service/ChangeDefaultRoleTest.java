package com.mohaeng.participant.application.service;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.application.usecase.ChangeDefaultRoleUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.ALREADY_DEFAULT_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_DEFAULT_ROLE;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.generalRole;
import static com.mohaeng.common.fixtures.ClubRoleFixture.officerRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;

@ApplicationTest
@DisplayName("ChangeDefaultRole 는 ")
class ChangeDefaultRoleTest {

    @Autowired
    private ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

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

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @ParameterizedTest(name = "[{arguments}] 회장과 임원은 기본 역할을 변경할 수 있으며, 기존의 기본 역할은 더이상 기본 역할이 아니게 된다.")
        @EnumSource(mode = INCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void success_test_1(final ClubRoleCategory category) {
            // given
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(category));

            ClubRole officerRole = clubRoleRepository.save(officerRole("기본 역할이 될 임원 역할", club));
            ClubRole generalRole = clubRoleRepository.save(generalRole("기본 역할이 될 일반 역할", club));

            List<ClubRole> beDefaultRole = List.of(officerRole, generalRole);

            // when
            beDefaultRole.forEach(it ->
                    changeDefaultRoleUseCase.command(
                            new ChangeDefaultRoleUseCase.Command(member.id(), it.id())
                    )
            );

            // then
            beDefaultRole.forEach(it -> {
                assertThat(clubRoleRepository.findById(clubRoleCategoryClubRoleMap.get(it.clubRoleCategory()).id()).get().isDefault()).isFalse();
                assertThat(clubRoleRepository.findById(it.id()).get().isDefault()).isTrue();
            });
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @ParameterizedTest(name = "[{arguments}] 회장과 임원이 아니라면 기본 역할을 변경할 수 없다.")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"PRESIDENT", "OFFICER"})
        void fail_test_1(final ClubRoleCategory category) {
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(category));

            ClubRole officerRole = clubRoleRepository.save(officerRole("기본 역할이 될 임원 역할", club));
            ClubRole generalRole = clubRoleRepository.save(generalRole("기본 역할이 될 일반 역할", club));

            List<ClubRole> beDefaultRole = List.of(officerRole, generalRole);

            // when
            beDefaultRole.stream().map(it ->
                    assertThrows(ClubRoleException.class, () -> {
                        changeDefaultRoleUseCase.command(
                                new ChangeDefaultRoleUseCase.Command(member.id(), it.id())
                        );
                    }).exceptionType()
            ).forEach(it -> {
                // then
                assertThat(it).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);
            });
        }

        @Test
        @DisplayName("이미 기본 역할인 경우 예외가 발생한다.")
        void fail_test_2() {
            Member member = saveMember();
            Club club = saveClub();
            Map<ClubRoleCategory, ClubRole> clubRoleCategoryClubRoleMap = saveDefaultClubRoles(club);
            Participant participant = saveParticipant(member, club, clubRoleCategoryClubRoleMap.get(PRESIDENT));

            // when
            clubRoleCategoryClubRoleMap.values().stream().map(it ->
                    assertThrows(ClubRoleException.class, () -> {
                        changeDefaultRoleUseCase.command(
                                new ChangeDefaultRoleUseCase.Command(member.id(), it.id())
                        );
                    }).exceptionType()
            ).forEach(it -> {
                // then
                assertThat(it).isEqualTo(ALREADY_DEFAULT_ROLE);
            });
        }
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