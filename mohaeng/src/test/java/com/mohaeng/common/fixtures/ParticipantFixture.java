package com.mohaeng.common.fixtures;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.springframework.test.util.ReflectionTestUtils;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ParticipantFixture {

    public static Participant mockOfficer(final Club club) {
        Participant officer = mock(Participant.class);
        given(officer.isManager()).willReturn(true);
        given(officer.isPresident()).willReturn(false);
        given(officer.club()).willReturn(club);
        return officer;
    }

    public static Participant mockGeneral(final Club club) {
        Participant general = mock(Participant.class);
        given(general.isManager()).willReturn(false);
        given(general.isPresident()).willReturn(false);
        given(general.club()).willReturn(club);
        return general;
    }

    public static Participant mockPresident(final Club club) {
        Participant president = mock(Participant.class);
        given(president.club()).willReturn(club);
        given(president.isManager()).willReturn(true);
        given(president.isPresident()).willReturn(true);
        return president;
    }

    public static Participant saveOfficer(final MemberRepository memberRepository, final Club club) {
        return club.participants().register(saveMember(memberRepository, member(null)), club, club.findDefaultRoleByCategory(OFFICER));
    }

    public static Participant saveGeneral(final MemberRepository memberRepository, final Club club) {
        return club.participants().register(saveMember(memberRepository, member(null)), club, club.findDefaultRoleByCategory(GENERAL));
    }

    public static Participant participantWithId(final Long id, final Member member, final Club club, final ClubRole clubRole) {
        Participant participant = new Participant(member, club, clubRole);
        ReflectionTestUtils.setField(participant, "id", id);
        return participant;
    }
}
