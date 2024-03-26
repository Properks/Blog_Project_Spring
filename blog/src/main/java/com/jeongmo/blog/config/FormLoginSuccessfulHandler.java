package com.jeongmo.blog.config;

import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.service.RefreshTokenService;
import com.jeongmo.blog.util.cookie.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class FormLoginSuccessfulHandler implements AuthenticationSuccessHandler{
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh_Token";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        refreshTokenService.saveOrUpdateRefreshToken(user.getId(), refreshToken);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        response.sendRedirect("/home?token=" + accessToken);
    }
}
