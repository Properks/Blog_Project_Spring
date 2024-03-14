package com.jeongmo.blog.config.oauth;

import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.service.RefreshTokenService;
import com.jeongmo.blog.util.cookie.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessfulHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh_Token";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);
    private static final String REDIRECT_URL = "/home";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        refreshTokenService.saveOrUpdateRefreshToken(user.getId(), refreshToken);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String url = getRedirectUrl(accessToken);

        super.clearAuthenticationAttributes(request);

        getRedirectStrategy().sendRedirect(request, response, url);

    }

    private String getRedirectUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("token", accessToken)
                .build()
                .toUriString();
    }
}
