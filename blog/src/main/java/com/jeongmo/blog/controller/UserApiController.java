package com.jeongmo.blog.controller;

import com.jeongmo.blog.domain.User;
import com.jeongmo.blog.dto.user.*;
import com.jeongmo.blog.service.ArticleService;
import com.jeongmo.blog.service.UserService;
import com.jeongmo.blog.util.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    private final ArticleService articleService;
    private final SecurityUtils securityUtils;

    @PostMapping("/user")
    public String signup(@RequestParam("email") String email, @RequestParam("password") String password,
                         @RequestParam("nickname") String nickname) {
        userService.save(new AddUserRequest(email, password, nickname));
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAccount(HttpServletRequest request,
    HttpServletResponse response, @PathVariable Long userId) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user.getId().equals(userId)) {
                articleService.deleteArticlesByAuthorId(user.getId());
                //logout
                SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
                handler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
                //delete account
                userService.delete(userId);
            }
            // redirect in frontend.
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/user/nickname")
    public ResponseEntity<UserResponse> updateAccountNickname(@RequestBody UpdateAccountNickname nicknameDto) {
        User user = userService.updateNickname(nicknameDto);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            // Cannot update because nickname already exists. (409 error)
        }
        securityUtils.updateUserInfo(user);
        return ResponseEntity.ok().body(new UserResponse(user));
    }

    @PutMapping("/user/password")
    public ResponseEntity<UserResponse> updateAccountPassword(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              @RequestBody UpdateAccountPassword passwordDto) {
        User user = userService.updatePassword(passwordDto);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            // Incorrect old password (401 error)
        }
        logout(request, response);
        // Redirect login page in frontend
        return ResponseEntity.ok().body(new UserResponse(user));
    }

    @GetMapping("/api/email/{email}")
    public ResponseEntity<Void> validEmail(@PathVariable String email) {
        if (userService.isDuplicatedEmail(email)) {
            return ResponseEntity.status(HttpStatus.FOUND).build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/password")
    public ResponseEntity<Void> validPassword(@RequestBody PasswordRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (userService.isValidPassword(user.getId(), request.getPassword())) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid password (401 error)
    }

    @PostMapping("/api/user")
    public ResponseEntity<UserResponse> getAuthentication(Authentication authentication) {
        boolean isNotAuthenticated =
                authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof User);
        if (isNotAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponse response = new UserResponse((User) authentication.getPrincipal());

        return ResponseEntity.ok().body(response);
    }
}
