package com.jeongmo.blog.service;

import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.domain.RefreshToken;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.repository.RefreshTokenRepository;
import com.jeongmo.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2);

    public RefreshToken saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        RefreshToken foundToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(refreshToken))
                .orElse(RefreshToken.builder()
                        .userId(userId)
                        .refreshToken(refreshToken)
                        .build());
        return refreshTokenRepository.save(foundToken);
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not found refresh token (findByRefreshToken(), refreshToken = " + refreshToken + ")"
                ));
    }

    public RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not found refresh token (findByUserId(), userId = " + userId + ")"
                ));
    }

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) {
            return null;
        }
        Long userId = tokenProvider.getUserId(refreshToken);
        RefreshToken foundToken = findByUserId(userId);
        if (!refreshToken.equals(foundToken.getRefreshToken())) {
            return null;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not found user (createNewAccessToken(), userId = " + userId + ")"
                ));
        return tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
    }

}
