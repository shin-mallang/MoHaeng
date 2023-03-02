package com.mohaeng.club.club.application.service.query;

import com.mohaeng.club.club.application.usecase.query.ExportParticipantsToExcelUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubExceptionType;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.util.ExcelExporter;
import com.mohaeng.common.util.ExcelExporter.ExcelRow;
import com.mohaeng.common.util.ExcelExporter.HeaderTitles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL;

@Service
@Transactional(readOnly = true)
public class ExportParticipantsToExcel implements ExportParticipantsToExcelUseCase {

    private static final String WORK_SHEET_NAME_FORMAT = "%s의 참여자 명단";

    private final ExcelExporter excelExporter;
    private final ClubRepository clubRepository;

    public ExportParticipantsToExcel(final ExcelExporter excelExporter, final ClubRepository clubRepository) {
        this.excelExporter = excelExporter;
        this.clubRepository = clubRepository;
    }

    @Override
    public void export(final Query query) {
        final Club club = clubRepository.findById(query.clubId()).orElseThrow(() -> new ClubException(ClubExceptionType.NOT_FOUND_CLUB));
        validateRequesterIsManager(query.memberId(), club);

        final HeaderTitles headerTitles = new HeaderTitles("이름", "나이", "성별", "가입일", "모임에서의 역할", "역할의 권한");
        final List<ExcelRow> excelRows = makeParticipantsExcelRows(club);
        excelExporter.export(excelRows, headerTitles, query.os(), WORK_SHEET_NAME_FORMAT.formatted(club.name()));
    }

    private void validateRequesterIsManager(final Long memberId, final Club club) {
        final Participant participant = club.findParticipantByMemberId(memberId);
        if (!participant.isManager()) {
            throw new ParticipantException(NO_AUTHORITY_EXPORT_PARTICIPANT_TO_EXCEL);
        }
    }

    private List<ExcelRow> makeParticipantsExcelRows(final Club club) {
        return club.participants()
                .participants()
                .stream()
                .map(this::makeParticipantExcelRow)
                .toList();
    }

    private ExcelRow makeParticipantExcelRow(final Participant participant) {
        return new ExcelRow(
                participant.member().name(),
                String.valueOf(participant.member().age()),
                participant.member().gender().name(),
                participant.createdAt().toString(),
                participant.clubRole().name(),
                participant.clubRole().clubRoleCategory().name()
        );
    }
}
