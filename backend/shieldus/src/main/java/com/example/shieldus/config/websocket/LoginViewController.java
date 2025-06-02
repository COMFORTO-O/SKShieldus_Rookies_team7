package com.example.shieldus.config.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/")
    public String loginPage() {
        return "login"; // templates/login.html 렌더링
    }

    @GetMapping("/login")
    public String loginRedirect() {
        return "redirect:/";
    }
}