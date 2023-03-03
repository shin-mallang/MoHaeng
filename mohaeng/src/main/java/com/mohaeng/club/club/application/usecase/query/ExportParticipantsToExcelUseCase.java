package com.mohaeng.club.club.application.usecase.query;

import java.io.OutputStream;

public interface ExportParticipantsToExcelUseCase {

    void export(final Query query);

    record Query(
            OutputStream os,
            Long memberId,
            Long clubId
    ) {
    }
}
