package com.mohaeng.club.club.infrastructure.export.excel;

import com.mohaeng.club.club.application.usecase.query.ExportParticipantsToExcelUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.util.ExcelExporter;
import com.mohaeng.common.util.RepositoryUtil;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayOutputStream;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ExportParticipantsToExcel(모임의 참가자를 엑셀로 내보내는 기능) 은")
@ApplicationTest
class ExportParticipantsToExcelTest {

    @Autowired
    private ExportParticipantsToExcelUseCase exportParticipantsToExcelUseCase;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @MockBean
    private ExcelExporter excelExporter;

    private Member presidentMember;
    private Club club;
    private Member officerMember;
    private Member generalMember;
    private Participant officer;
    private Participant general;

    @BeforeEach
    void init() {
        presidentMember = saveMember(memberRepository, member(null));
        club = RepositoryUtil.saveClub(clubRepository, clubWithMember(presidentMember));
        officerMember = memberRepository.save(member(null));
        generalMember = memberRepository.save(member(null));
        officer = club.participants().register(officerMember, club, club.findDefaultRoleByCategory(ClubRoleCategory.OFFICER));
        general = club.participants().register(generalMember, club, club.findDefaultRoleByCategory(ClubRoleCategory.GENERAL));
        em.flush();
        em.clear();
    }

    @Test
    void 모임이_없는_경우_예외를_발생시킨다() {
        // when
        final BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                exportParticipantsToExcelUseCase.export(
                        new ExportParticipantsToExcelUseCase.Query(new ByteArrayOutputStream(), presidentMember.id(), 1233L)
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
        then(excelExporter).shouldHaveNoInteractions();
    }

    @Test
    void 요청자가_임원_혹은_회장이_아닌_경우_예외를_발생시킨다() {
        // when
        final BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                exportParticipantsToExcelUseCase.export(
                        new ExportParticipantsToExcelUseCase.Query(new ByteArrayOutputStream(), generalMember.id(), club.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL);
        then(excelExporter).shouldHaveNoInteractions();
    }

    @Test
    void 엑셀_파일을_생성한다() {
        // when
        exportParticipantsToExcelUseCase.export(
                new ExportParticipantsToExcelUseCase.Query(new ByteArrayOutputStream(), officer.member().id(), club.id())
        );

        // then
        then(excelExporter).should().export(any(), any(), any(), any());
    }
}