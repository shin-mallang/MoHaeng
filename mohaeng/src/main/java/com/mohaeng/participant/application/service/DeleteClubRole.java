package com.mohaeng.participant.application.service;

import com.mohaeng.clubrole.application.usecase.DeleteClubRoleUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.NOT_FOUND_CLUB_ROLE;
import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

@Service
@Transactional
public class DeleteClubRole implements DeleteClubRoleUseCase {

    private final ClubRoleRepository clubRoleRepository;
    private final ParticipantRepository participantRepository;

    public DeleteClubRole(final ClubRoleRepository clubRoleRepository, final ParticipantRepository participantRepository) {
        this.clubRoleRepository = clubRoleRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void command(final Command command) {
        ClubRole clubRole = clubRoleRepository.findWithClubById(command.clubRoleId())
                .orElseThrow(() -> new ClubRoleException(NOT_FOUND_CLUB_ROLE));
        Participant requester = participantRepository.findWithClubRoleByMemberIdAndClub(command.memberId(), clubRole.club())
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));

        // 기본 역할이 제거되는 경우, 해당 범주에 속하는 임의의 역할을 기본 역할로 지정
        replaceDefaultRole(clubRole);

        requester.deleteClubRole(clubRole);

        // 제거되는 역할을 가지고 있는 기존 회원들은 해당 범주에 속하는 기본 역할로 역할이 변경된다.
        changeParticipantsRoleToDefaultRole(clubRole);

        clubRoleRepository.delete(clubRole);
    }

    /**
     * 기본 역할이 제거되는 경우, 해당 범주에 속하는 임의의 역할을 기본 역할로 지정한다.
     */
    private void replaceDefaultRole(final ClubRole clubRole) {
        if (clubRole.isDefault()) {
            List<ClubRole> defaultRoleCandidates = clubRoleRepository.findTop2ByClubAndClubRoleCategory(clubRole.club(), clubRole.clubRoleCategory());

            if (defaultRoleCandidates.size() < 2) {
                // 기본 역할을 포함한 해당 범주에 속하는 역할이 1개인 경우 대체할 역할이 없다는 것이므로 제거할 수 없다.
                throw new ClubRoleException(CAN_NOT_DELETE_ROLE_BECAUSE_NO_ROLE_TO_REPLACE);
            }
            defaultRoleCandidates.remove(clubRole);

            // 기본 역할로 변경
            defaultRoleCandidates.get(0).makeDefault();
        }
    }

    /**
     * 제거되는 역할을 가지고 있는 기존 회원들의 역할을 해당 범주에 속하는 기본 역할로 변경한다.
     */
    private void changeParticipantsRoleToDefaultRole(final ClubRole clubRole) {
        List<Participant> changeRoleTargets = participantRepository.findAllByClubRole(clubRole);
        ClubRole defaultRole = clubRoleRepository.findDefaultRoleByClubAndClubRoleCategory(clubRole.club(), clubRole.clubRoleCategory());
        changeRoleTargets.forEach(it -> it.changeRole(defaultRole));
    }
}
