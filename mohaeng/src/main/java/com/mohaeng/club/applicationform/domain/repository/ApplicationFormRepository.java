package com.mohaeng.club.applicationform.domain.repository;

import com.mohaeng.club.applicationform.domain.model.ApplicationForm;

import java.util.Optional;

public interface ApplicationFormRepository {

    ApplicationForm save(final ApplicationForm applicationForm);

    Optional<ApplicationForm> findById(final Long id);
}
