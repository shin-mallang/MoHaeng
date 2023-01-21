package com.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;

@Disabled
@DisplayName("수퍼타입토큰 테스트")
public class SuperTypeTokenTest {

    interface Sup<T> {
        @SuppressWarnings("unchecked")
        default Class<T> supportType() {
            return (Class<T>) (((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
        }
    }

    static class Sub implements Sup<String> {
    }

    static abstract class Sup2<T> {
        @SuppressWarnings("unchecked")
        public Class<T> supportType() {
            return (Class<T>) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        }
    }

    static class Sub2 extends Sup2<String> {
    }

    @Test
    @DisplayName("인터페이스 테스트")
    void test1() {
        Sub b = new Sub();
        Assertions.assertThat(b.supportType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("추상클래스 테스트")
    void test2() {
        Sub2 b = new Sub2();
        Assertions.assertThat(b.supportType()).isEqualTo(String.class);
    }
}
