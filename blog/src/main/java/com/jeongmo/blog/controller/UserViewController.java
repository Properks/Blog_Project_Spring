package com.jeongmo.blog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class UserViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signUpPage() {
        return "signup";
    }

    /**
     * Controller that user can see and modify user information
     *
     * @param tab The tab which user selects (tab = "my_info"or "change_password")
     */
    @GetMapping("/user/info")
    public String userInformation(Model model, @RequestParam(required = false, defaultValue = "my_info") String tab) {
        if (!tab.equals("my_info") && !tab.equals("change_password") && !tab.equals("delete_account")) {
            throw new IllegalArgumentException("Invalid tab name");
        } else {
            model.addAttribute("tab", tab);
        }
        return "userInformation";
    }
}
