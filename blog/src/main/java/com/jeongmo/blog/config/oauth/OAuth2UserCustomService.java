package com.jeongmo.blog.config.oauth;

import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        saveOrUpdateUser(oAuth2User);
        return oAuth2User;
    }

    public User saveOrUpdateUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");
        Optional<User> optional = userRepository.findByEmail(email);
        User user;
        if (optional.isPresent()) {
            user = optional.get();
            user.setNickname(nickname);
        }
        else {
            user = User.builder().email(email).nickname(nickname).build();
        }
        return userRepository.save(user);
    }
}
