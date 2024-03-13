package com.jeongmo.blog.service;

import com.jeongmo.blog.config.jwt.JwtProperties;
import com.jeongmo.blog.config.jwt.TokenProvider;
import com.jeongmo.blog.domain.RefreshToken;
import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.repository.RefreshTokenRepository;
import com.jeongmo.blog.repository.UserRepository;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RefreshTokenServiceTest {

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    User user;

    @BeforeEach
    void init() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("test@email.com")
                .password(bCryptPasswordEncoder.encode("test1234"))
                .build());
    }

    @Test
    void saveOrUpdateRefreshToken() {
        //given
        final Long userId = user.getId();
        final String token = createRefreshToken();

        //when
        RefreshToken refreshToken = refreshTokenService.saveOrUpdateRefreshToken(userId, token);

        //then
        RefreshToken foundToken = refreshTokenRepository.findById(refreshToken.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Not found refresh token (saveOrUpdateRefreshTokenTest())"
                ));
        assertThat(foundToken.getUserId()).isEqualTo(userId);
        assertThat(foundToken.getRefreshToken()).isEqualTo(token);

    }

    @Test
    void findByRefreshToken() {
        //given
        final Long userId = user.getId();
        final String token = createRefreshToken();
        final RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(userId)
                        .refreshToken(token)
                        .build());

        //when
        RefreshToken result = refreshTokenService.findByRefreshToken(token);

        //then
        assertThat(result.getId()).isEqualTo(refreshToken.getId());
        assertThat(result.getUserId()).isEqualTo(refreshToken.getUserId());

    }

    @Test
    void findByUserId() {
        //given
        final Long userId = user.getId();
        final String token = createRefreshToken();
        final RefreshToken refreshToken = refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(userId)
                        .refreshToken(token)
                        .build());

        //when
        RefreshToken result = refreshTokenService.findByUserId(userId);

        //then
        assertThat(result.getId()).isEqualTo(refreshToken.getId());
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken.getRefreshToken());

    }

    @Test
    void createNewAccessToken() {
        //given
        final Long userId = user.getId();
        final String token = createRefreshToken();
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(userId)
                        .refreshToken(token)
                        .build());

        //when
        String accessToken = refreshTokenService.createNewAccessToken(token);

        //then
        assertThat(tokenProvider.validToken(accessToken)).isTrue();
    }

    String createRefreshToken() {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getEmail())
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + Duration.ofDays(1).toMillis()))
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }
}