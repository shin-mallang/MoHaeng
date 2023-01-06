package com.mohaeng.applicationform.domain.repository;

import com.mohaeng.applicationform.domain.model.ApplicationForm;

import java.util.List;
import java.util.Optional;

public interface ApplicationFormRepository {

    ApplicationForm save(final ApplicationForm applicationForm);

    Optional<ApplicationForm> findById(final Long id);

    List<ApplicationForm> findAll();
}
