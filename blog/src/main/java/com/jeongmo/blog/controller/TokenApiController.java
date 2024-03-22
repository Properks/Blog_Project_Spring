package com.jeongmo.blog.controller;

import com.jeongmo.blog.dto.token.AccessTokenRequest;
import com.jeongmo.blog.dto.token.AccessTokenResponse;
import com.jeongmo.blog.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private RefreshTokenService refreshTokenService;

    @PostMapping("/api/token")
    public ResponseEntity<AccessTokenResponse> createAccess(@RequestBody AccessTokenRequest request) {
        String accessToken = refreshTokenService.createNewAccessToken(request.getRefreshToken());
        AccessTokenResponse response = new AccessTokenResponse(accessToken);
        return ResponseEntity.ok().body(response);
    }
}
