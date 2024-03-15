package com.jeongmo.blog.config.oauth;


import com.jeongmo.blog.util.cookie.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class OAuth2BasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String AUTH_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_AGE = 18000;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, AUTH_COOKIE_NAME);
        if (cookie == null) {
            return null;
        }
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookie(request, response);
            return;
        }
        CookieUtil.addCookie(response, AUTH_COOKIE_NAME, CookieUtil.serializeCookie(authorizationRequest), COOKIE_AGE);
    }

    private void removeAuthorizationRequestCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, AUTH_COOKIE_NAME);
    }
}
