package com.jeongmo.blog.config.oauth;

import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.service.CustomUserDetailService;
import com.jeongmo.blog.service.RefreshTokenService;
import com.jeongmo.blog.service.UserService;
import com.jeongmo.blog.util.cookie.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final CustomUserDetailService customUserDetailService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh_Token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "Access_Token";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(1);
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);
    private static final String REDIRECT_URL = "/home";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2user = (OAuth2User) authentication.getPrincipal();
        User user = (User) customUserDetailService.loadUserByUsername(oAuth2user.getAttribute("email"));

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        refreshTokenService.saveOrUpdateRefreshToken(user.getId(), refreshToken);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, (int) REFRESH_TOKEN_DURATION.toSeconds());

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, (int) ACCESS_TOKEN_DURATION.toSeconds());

        super.clearAuthenticationAttributes(request);

        response.sendRedirect(REDIRECT_URL);
    }
}
