package com.jeongmo.blog.util.security;

import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.user.UserResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class SecurityUtils {

    /**
     * Check and add authentication information to model
     *
     */
    public void checkAndAddLoginInfo(Model model, Authentication authentication) {
        if (authentication!= null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("loginIn", new UserResponse(user));
        }
    }

    public void updateUserInfo(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities())
        );
    }
}
