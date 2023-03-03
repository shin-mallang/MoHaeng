package com.mohaeng.club.club.presentation.query;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.query.ExportParticipantsToExcelUseCase;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
public class ExportParticipantToExcelController {

    public static final String EXPORT_PARTICIPANT_TO_EXCEL_URL = "/api/club/{clubId}/participant/export/excel";

    private final ExportParticipantsToExcelUseCase exportParticipantsToExcelUseCase;

    public ExportParticipantToExcelController(final ExportParticipantsToExcelUseCase exportParticipantsToExcelUseCase) {
        this.exportParticipantsToExcelUseCase = exportParticipantsToExcelUseCase;
    }

    @GetMapping(EXPORT_PARTICIPANT_TO_EXCEL_URL)
    public void export(
            @Auth final Long memberId,
            @PathVariable("clubId") Long clubId,
            HttpServletResponse response
    ) throws IOException {
        exportParticipantsToExcelUseCase.export(
                new ExportParticipantsToExcelUseCase.Query(response.getOutputStream(), memberId, clubId)
        );
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(UTF_8.name());
        final String name = URLEncoder.encode("participants-information", UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + name + ".xlsx");
    }
}
