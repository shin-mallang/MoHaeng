package com.mohaeng.club.applicationform.domain.repository;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;

import java.util.Optional;

public interface ApplicationFormRepository {

    ApplicationForm save(final ApplicationForm applicationForm);

    Optional<ApplicationForm> findById(final Long id);

    boolean existsByApplicantAndClubAndProcessedFalse(final Member applicant, final Club club);
}
