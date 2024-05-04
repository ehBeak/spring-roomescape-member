package roomescape.controller.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import roomescape.exception.BadRequestException;

class ReservationRequestTest {

    @Nested
    class ReservationRequestNameTest {

        @DisplayName("예약자명이 null인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_name_is_null() {
            assertThatThrownBy(() -> new ReservationRequest(null, LocalDate.of(2030, 8, 9), 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 이름(name)이 [null]일 수 없습니다.");
        }

        @DisplayName("예약자명이 빈 문자열인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_name_is_empty() {
            assertThatThrownBy(() -> new ReservationRequest("", LocalDate.of(2030, 3, 3), 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 이름(name)이 []일 수 없습니다.");
        }
    }

    @Nested
    class ReservationRequestDateTest {

        @DisplayName("날짜가 null인 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_date_is_null() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 날짜(date)가 [null]일 수 없습니다.");
        }

        @DisplayName("날짜가 비어있는 경우 예외를 발생시킨다.")
        @Test
        void should_throw_exception_when_date_is_empty() {
            assertThatThrownBy(() -> new ReservationRequest("a", null, 1L, 1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("[ERROR] 날짜(date)가 [null]일 수 없습니다.");
        }
    }
}
