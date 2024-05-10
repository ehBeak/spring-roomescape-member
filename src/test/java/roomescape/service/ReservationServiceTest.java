package roomescape.service;

import static java.time.LocalDate.now;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import roomescape.controller.request.ReservationRequest;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.User;
import roomescape.service.fake.FakeReservationDao;
import roomescape.service.fake.FakeReservationTimeDao;
import roomescape.service.fake.FakeThemeDao;
import roomescape.service.fake.FakeUserDao;

class ReservationServiceTest {

    private final FakeThemeDao fakeThemeDao = new FakeThemeDao();
    private final FakeReservationTimeDao fakeReservationTimeDao = new FakeReservationTimeDao();
    private final FakeReservationDao fakeReservationDao = new FakeReservationDao();

    private final FakeUserDao fakeUserDao = new FakeUserDao();
    private final ReservationService reservationService =
            new ReservationService(fakeReservationDao, fakeReservationTimeDao, fakeThemeDao, fakeUserDao);

    @BeforeEach
    void setUp() {//todo 기본값 수정
        fakeThemeDao.clear();
        fakeReservationTimeDao.clear();
        fakeReservationDao.clear();

        fakeThemeDao.addTheme(new Theme(1L, "name1", "description1", "thumbnail1"));
        fakeThemeDao.addTheme(new Theme(2L, "name2", "description2", "thumbnail2"));
        fakeReservationTimeDao.add(new ReservationTime(1L, LocalTime.of(10, 0)));
        fakeReservationTimeDao.add(new ReservationTime(2L, LocalTime.of(12, 0)));
        fakeUserDao.addUser(new User(1L, "썬", "sun@email.com", "1111"));
        fakeUserDao.addUser(new User(2L, "배키", "dmsgml@email.com", "2222"));
    }

    @DisplayName("모든 예약 시간을 반환한다")
    @Test
    void should_return_all_reservation_times() {
        Theme theme1 = new Theme(1L, "name1", "description1", "thumbnail1");
        Theme theme2 = new Theme(2L, "name2", "description2", "thumbnail2");
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        User user = new User(2L, "배키", "dmsgml@email.com", "2222");
        fakeReservationDao.addReservation(new Reservation(1L, now(), reservationTime, theme1, user));
        fakeReservationDao.addReservation(new Reservation(2L, now(), reservationTime, theme2, user));

        List<Reservation> reservations = reservationService.findAllReservations();

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("예약 시간을 추가한다")
    @Test
    void should_add_reservation_times() {
        ReservationRequest request = new ReservationRequest(now().plusDays(2), 1L, 1L, 1L);

        reservationService.addReservation(request);

        List<Reservation> allReservations = fakeReservationDao.getAllReservations();
        assertThat(allReservations).hasSize(1);
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_remove_reservation_times() {
        Theme theme1 = new Theme(1L, "name1", "description1", "thumbnail1");
        Theme theme2 = new Theme(2L, "name2", "description2", "thumbnail2");
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        User user = new User(2L, "배키", "dmsgml@email.com", "2222");
        fakeReservationDao.addReservation(new Reservation(1L, now(), reservationTime, theme1, user));
        fakeReservationDao.addReservation(new Reservation(2L, now(), reservationTime, theme2, user));

        reservationService.deleteReservation(1L);

        List<Reservation> reservations = fakeReservationDao.getAllReservations();
        assertThat(reservations).hasSize(1);
    }

    @DisplayName("존재하지 않는 예약을 삭제하면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_not_exist_reservation_time() {
        assertThatThrownBy(() -> reservationService.deleteReservation(1000000))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 해당 id:[1000000] 값으로 예약된 내역이 존재하지 않습니다.");
    }

    @DisplayName("존재하는 예약을 삭제하면 예외가 발생하지 않는다.")
    @Test
    void should_not_throw_exception_when_exist_reservation_time() {
        Theme theme1 = new Theme(1L, "name1", "description1", "thumbnail1");
        Theme theme2 = new Theme(2L, "name2", "description2", "thumbnail2");
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        User user = new User(2L, "배키", "dmsgml@email.com", "2222");
        fakeReservationDao.addReservation(new Reservation(1L, now(), reservationTime, theme1, user));
        fakeReservationDao.addReservation(new Reservation(2L, now(), reservationTime, theme2, user));

        assertThatCode(() -> reservationService.deleteReservation(1))
                .doesNotThrowAnyException();
    }

    @DisplayName("현재 이전으로 예약하면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_previous_date() {
        ReservationRequest request =
                new ReservationRequest(LocalDate.now().minusDays(1), 1L, 1L, 1L);

        assertThatThrownBy(() -> reservationService.addReservation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("[ERROR] 현재(", ") 이전 시간으로 예약할 수 없습니다.");
    }

    @DisplayName("현재로 예약하면 예외가 발생하지 않는다.")
    @Test
    void should_not_throw_exception_when_current_date() {
        fakeReservationTimeDao.add(new ReservationTime(3L, LocalTime.now()));
        ReservationRequest request = new ReservationRequest(LocalDate.now(), 3L, 1L, 1L);

        assertThatCode(() -> reservationService.addReservation(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("현재 이후로 예약하면 예외가 발생하지 않는다.")
    @Test
    void should_not_throw_exception_when_later_date() {
        ReservationRequest request =
                new ReservationRequest(LocalDate.now().plusDays(2), 1L, 1L, 1L);

        assertThatCode(() -> reservationService.addReservation(request))
                .doesNotThrowAnyException();
    }

    @DisplayName("날짜, 시간, 테마가 일치하는 예약을 추가하려 할 때 예외가 발생한다.")
    @Test
    void should_throw_exception_when_add_exist_reservation() {
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        Theme theme = new Theme(1L, "name1", "description1", "thumbnail1");
        LocalDate date = now().plusDays(2);
        User user = new User(2L, "배키", "dmsgml@email.com", "2222");
        fakeReservationDao.addReservation(new Reservation(1L, date, reservationTime, theme, user));

        ReservationRequest request = new ReservationRequest(date, 1L, 1L, 2L);
        assertThatThrownBy(() -> reservationService.addReservation(request))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("[ERROR] 이미 해당 시간에 예약이 존재합니다.");
    }

    @DisplayName("사용자 아이디 값이 존재하지 않는다면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_add_not_exist_user() {
        ReservationTime reservationTime = new ReservationTime(1L, LocalTime.of(10, 0));
        Theme theme = new Theme(2L, "name2", "description2", "thumbnail2");
        LocalDate date = now().plusDays(2);
        User user = new User(2L, "배키", "dmsgml@email.com", "2222");
        fakeReservationDao.addReservation(new Reservation(1L, date, reservationTime, theme, user));

        ReservationRequest request = new ReservationRequest(date, 1L, 1L, 3L);
        assertThatThrownBy(() -> reservationService.addReservation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 아이디가 3인 사용자는 존재하지 않습니다.");
    }
}
