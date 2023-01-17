package com.mohaeng.applicationform.application.helper;

import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.repository.ParticipantRepository;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;

/**
 * 도메인 서비스..? OR Helper Method?
 */
public class ApplicantAlreadyJoinClubCheckHelper {

    /**
     * 이미 신청자가 모임에 가입되어 있는지 검사한다.
     *
     * @throws ApplicationFormException 이미 신청자가 모임에 가입되어 있는 경우
     */
    public static void checkApplicantAlreadyJoinClub(final ParticipantRepository participantRepository,
                                                     final Member applicant,
                                                     final Club target) throws ApplicationFormException {
        if (participantRepository.existsByMemberAndClub(applicant, target)) {
            throw new ApplicationFormException(ALREADY_MEMBER_JOINED_CLUB);
        }
    }
}
