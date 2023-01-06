package com.mohaeng.common.domain;

import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DisplayName("BaseEntity 는 ")
class BaseEntityTest {

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("저장될 때 createdAt과 lastModifiedAt이 함께 저장된다")
    void saveCreatedAtAndLastModifiedAtWhenSaved() {
        // given
        TestEntity entity = new TestEntity();

        // when
        em.persist(entity);

        // then
        assertAll(
                () -> assertThat(entity.createdAt()).isNotNull(),
                () -> assertThat(entity.lastModifiedAt()).isNotNull()
        );
    }

    @Test
    @DisplayName("수정될 때 lastModifiedAt이 수정된다.")
    void saveLastModifiedAtWhenModified() {
        // given
        TestEntity entity = new TestEntity();
        em.persist(entity);

        LocalDateTime before = entity.lastModifiedAt();

        // when
        entity.changeName("change");
        em.flush();
        em.clear();

        // then
        assertAll(
                () -> assertThat(entity.createdAt()).isEqualTo(before),
                () -> assertThat(entity.lastModifiedAt()).isNotEqualTo(before)
        );
    }

    @Entity
    private static class TestEntity extends BaseEntity {

        private String name = "default";

        public void changeName(String change) {
            this.name = change;
        }
    }
}