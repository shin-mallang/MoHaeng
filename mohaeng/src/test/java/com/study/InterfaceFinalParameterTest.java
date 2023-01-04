package com.study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InterfaceFinalParameterTest {


    @Test
    @DisplayName("final이 붙은 않은 파라미터를 가진 인터페이스 메서드의 구현체가 final을 붙이지 않고 구현하여 그 값을 변경하는 경우에 대한 테스트")
    void test() {
        // given
        TestInterfaceImpl testInterface = new TestInterfaceImpl();

        // when
        testInterface.finalMethod(20);
        // 문제 없다.
    }

    interface TestInterface {
        void finalMethod(final int a);
    }

    class TestInterfaceImpl implements TestInterface {
        @Override
        public void finalMethod(int a) {
            a = 10;
            a = 20;
        }
    }
}
