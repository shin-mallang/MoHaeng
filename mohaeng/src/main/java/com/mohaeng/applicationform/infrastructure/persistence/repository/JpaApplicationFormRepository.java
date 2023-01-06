package com.mohaeng.applicationform.infrastructure.persistence.repository;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaApplicationFormRepository extends JpaRepository<ApplicationForm, Long>, ApplicationFormRepository {
}
