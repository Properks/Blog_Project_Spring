package com.jeongmo.blog.config.jwt;

import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    User user;

    @BeforeEach
    void init() {
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .email("test@email.com")
                .password(encoder.encode("test1234"))
                .nickname("test")
                .build());
    }

    @Test
    void generateToken() {
        //given
        final Duration expiredAt = Duration.ofDays(1);

        //when
        String token = tokenProvider.generateToken(user, expiredAt);

        //then
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
        assertThat(claims.get("id", Long.class)).isEqualTo(user.getId());
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    }

    @Test
    void createToken() {
        //given
        final Date expire = new Date(new Date().getTime() + Duration.ofDays(1).toMillis());

        //when
        String token = tokenProvider.createToken(user, expire);

        //then
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
        assertThat(claims.get("id", Long.class)).isEqualTo(user.getId());
        assertThat(claims.getSubject()).isEqualTo(user.getEmail());
    }

    @Test
    void validToken() {
        //given
        final Date expire = new Date(new Date().getTime() + Duration.ofDays(1).toMillis());
        final String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expire)
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isTrue();

    }

    @Test
    void getAuthentication() {
        //final
        final Date expire = new Date(new Date().getTime() + Duration.ofDays(1).toMillis());
        final String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expire)
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(user.getEmail());
    }

    @Test
    void getUserId() {
        //given
        final Date expire = new Date(new Date().getTime() + Duration.ofDays(1).toMillis());
        final String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expire)
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();

        //then
        Long userId = tokenProvider.getUserId(token);

        //when
        assertThat(userId).isEqualTo(user.getId());
    }
}