package roomescape.controller.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import roomescape.exception.BadRequestException;

class ReservationRequestTest {

    @Nested
    class ReservationRequestNameTest {

        @DisplayName("예약자명이 null인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_name_is_null() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 유효하지 않은 요청입니다.");
        }

        @DisplayName("예약자명이 빈 문자열인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_name_is_empty() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 유효하지 않은 요청입니다.");
        }
    }

    @Nested
    class ReservationRequestDateTest {

        @DisplayName("날짜가 null인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_date_is_null() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 유효하지 않은 요청입니다.");
        }

        @DisplayName("날짜가 비어있는 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_date_is_empty() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 유효하지 않은 요청입니다.");
        }

        @DisplayName("날짜의 형식이 유효하지 않은 경우 예외를 발생시킨다.")
        @ParameterizedTest
        @ValueSource(strings = {"2024:03:27", "2024/01/11", "에베", "12-12"})
        void should_throw_exception_when_date_is_bad_format(String date) {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 유효하지 않은 요청입니다.");
        }
    }
}
