package com.jeongmo.blog.repository;

import com.jeongmo.blog.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Repository
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    User user1;
    User user2;

    @BeforeEach
    void init() {
        userRepository.deleteAll();
    }

    @Test
    void save() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();

        //when
        userRepository.save(user1);

        //then
        List<User> users = userRepository.findAll();
        assertThat(users.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(users.get(0).getPassword()).isEqualTo(user1.getPassword());
    }

    @Test
    void findById() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();
        user1 = userRepository.save(user1);

        //when
        User result = userRepository.findById(user1.getId()).get();

        //then
        assertThat(result.getId()).isEqualTo(user1.getId());
        assertThat(result.getEmail()).isEqualTo(user1.getEmail());
        assertThat(result.getPassword()).isEqualTo(user1.getPassword());
        assertThat(result.getNickname()).isEqualTo(user1.getNickname());
    }

    @Test
    void findAll() {
        //given
        final String email1 = "test1@email.com";
        final String password1 = "test1234";
        final String nickname1 = "test1#12345";
        user1 = User.builder()
                .email(email1)
                .password(encoder.encode(password1))
                .nickname(nickname1)
                .build();
        user1 = userRepository.save(user1);

        final String email2 = "test2@email.com";
        final String password2 = "test1234";
        final String nickname2 = "test1#67890";
        user2 = User.builder()
                .email(email2)
                .password(encoder.encode(password2))
                .nickname(nickname2)
                .build();
        user2 = userRepository.save(user2);


        //when
        List<User> result = userRepository.findAll();

        //then

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(user1.getId());
        assertThat(result.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(result.get(0).getPassword()).isEqualTo(user1.getPassword());
        assertThat(result.get(0).getNickname()).isEqualTo(user1.getNickname());

        assertThat(result.get(1).getId()).isEqualTo(user2.getId());
        assertThat(result.get(1).getEmail()).isEqualTo(user2.getEmail());
        assertThat(result.get(1).getPassword()).isEqualTo(user2.getPassword());
        assertThat(result.get(1).getNickname()).isEqualTo(user2.getNickname());
    }

    @Test
    void deleteById() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();
        user1 = userRepository.save(user1);

        //when
        userRepository.deleteById(user1.getId());

        //then
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void deleteAll() {
        //given
        final String email1 = "test1@email.com";
        final String password1 = "test1234";
        final String nickname1 = "test1#12345";
        user1 = User.builder()
                .email(email1)
                .password(encoder.encode(password1))
                .nickname(nickname1)
                .build();
        user1 = userRepository.save(user1);

        final String email2 = "test2@email.com";
        final String password2 = "test1234";
        final String nickname2 = "test1#67890";
        user2 = User.builder()
                .email(email2)
                .password(encoder.encode(password2))
                .nickname(nickname2)
                .build();
        user2 = userRepository.save(user2);

        //when
        userRepository.deleteAll();

        //then
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }

    @Test
    void findByEmail() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();
        userRepository.save(user1);

        //when
        User result = userRepository.findByEmail(email).get();

        //then
        assertThat(result.getId()).isEqualTo(user1.getId());
        assertThat(result.getEmail()).isEqualTo(user1.getEmail());
        assertThat(result.getPassword()).isEqualTo(user1.getPassword());
        assertThat(result.getNickname()).isEqualTo(user1.getNickname());
    }

    @Test
    void existsByEmail() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();
        userRepository.save(user1);

        //when
        boolean result = userRepository.existsByEmail(email);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void existsByNickname() {
        //given
        final String email = "test1@email.com";
        final String password = "test1234";
        final String nickname = "test1#12345";
        user1 = User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .build();
        userRepository.save(user1);

        //when
        boolean result = userRepository.existsByNickname(nickname);

        //then
        assertThat(result).isTrue();
    }
}