package roomescape.service;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import org.springframework.stereotype.Service;

import roomescape.exception.AuthorizationException;
import roomescape.exception.BadRequestException;
import roomescape.model.User;

@Service
public class AuthService {

    private static final String COOKIE_NAME = "token";
    private final TokenProvider tokenProvider;

    public AuthService(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public Cookie createCookieByUser(User user) {
        String jwt = tokenProvider.createToken(user);
        Cookie cookie = new Cookie(COOKIE_NAME, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    public Long findUserNameByCookies(Cookie[] cookies) {
        Cookie cookie = findCookieIfExist(cookies);
        String payload = tokenProvider.getPayload(cookie.getValue());
        validateUser(payload);
        return Long.parseLong(payload);
    }

    private Cookie findCookieIfExist(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findAny()
                .orElseThrow(() -> new BadRequestException("아이디가 %s인 쿠키가 없습니다.".formatted(COOKIE_NAME)));
    }

    private void validateUser(String payLoad) {
        try {
            Long.parseLong(payLoad);
        } catch (NumberFormatException exception) {
            throw new AuthorizationException("인증되지 않은 사용자입니다.");
        }
    }
}
