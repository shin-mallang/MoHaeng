package com.mohaeng.applicationform.domain.repository;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface ApplicationFormRepository {

    ApplicationForm save(final ApplicationForm applicationForm);

    Optional<ApplicationForm> findById(final Long id);

    Optional<ApplicationForm> findWithClubById(final Long id);

    List<ApplicationForm> findAll();

    boolean existsByApplicantAndTargetAndProcessedFalse(final Member applicant, final Club target);
}
