package com.mohaeng.applicationform.infrastructure.persistence.repository;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaApplicationFormRepository extends JpaRepository<ApplicationForm, Long>, ApplicationFormRepository {

    @Override
    @Query("select af from ApplicationForm af join fetch af.applicant join fetch af.target where af.id = :id")
    Optional<ApplicationForm> findWithMemberAndClubById(@Param("id") final Long id);

    @Override
    @Query("select af from ApplicationForm af join fetch af.target where af.id = :id")
    Optional<ApplicationForm> findWithClubById(@Param("id") final Long id);
}
