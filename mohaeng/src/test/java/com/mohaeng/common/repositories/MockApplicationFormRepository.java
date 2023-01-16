package com.mohaeng.common.repositories;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockApplicationFormRepository implements ApplicationFormRepository {

    private final Map<Long, ApplicationForm> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public ApplicationForm save(final ApplicationForm applicationForm) {
        ReflectionTestUtils.setField(applicationForm, "id", ++sequence);
        store.put(applicationForm.id(), applicationForm);
        return applicationForm;
    }

    @Override
    public Optional<ApplicationForm> findById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ApplicationForm> findAll() {
        return store.values().stream().toList();
    }

    @Override
    public boolean existsByApplicantAndTarget(final Member applicant, final Club target) {
        return store.values().stream()
                .anyMatch(it -> it.applicant().id().equals(applicant.id()) && it.target().id().equals(target.id()));
    }

    @Override
    public Optional<ApplicationForm> findWithMemberAndClubById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<ApplicationForm> findWithClubById(Long id) {
        return Optional.empty();
    }
}
