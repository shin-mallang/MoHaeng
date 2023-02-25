package com.mohaeng.club.club.presentation.command;

import com.mohaeng.authentication.presentation.argumentresolver.Auth;
import com.mohaeng.club.club.application.usecase.command.LeaveClubUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveClubController {

    public static final String LEAVE_CLUB_URL = "/api/club/{clubId}/leave";

    private final LeaveClubUseCase leaveClubUseCase;

    public LeaveClubController(final LeaveClubUseCase leaveClubUseCase) {
        this.leaveClubUseCase = leaveClubUseCase;
    }

    /**
     * 참여자 제거(모임에서 탈퇴)
     */
    @DeleteMapping(path = LEAVE_CLUB_URL)
    public ResponseEntity<Void> leave(
            @PathVariable(name = "clubId") final Long clubId,
            @Auth final Long memberId
    ) {
        leaveClubUseCase.command(
                new LeaveClubUseCase.Command(memberId, clubId)
        );
        return ResponseEntity.ok().build();
    }
}
