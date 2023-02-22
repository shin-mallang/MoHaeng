package com.study;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Disabled
public class NestedTestInitTest {

    class SoutWhenInit {
        public SoutWhenInit() {
            System.out.println("생성됩니당");
        }
    }

    private SoutWhenInit soutWhenInit = new SoutWhenInit();

    @BeforeEach
    void init() {
        System.out.println("바깥 테스트 @BeforeEach");
    }

    @Nested
    class SuccessTest {

        private SoutWhenInit soutWhenInit = new SoutWhenInit();

        @BeforeEach
        void init() {
            System.out.println("내부 테스트 @BeforeEach");
        }

        @Test
        void a() {
            // given

            // when

            // then
        }
    }

    @Nested
    class OtherTest {

        private SoutWhenInit soutWhenInit = new SoutWhenInit();

        @BeforeEach
        void init() {
            System.out.println("내부 테스트 @BeforeEach2");
        }

        @Test
        void b() {
            // given

            // when

            // then
        }
    }
}
