package com.ensah.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

public class LoginController {

    @Controller
    public class AuthController {

        @GetMapping("/login")
        public String loginPage() {
            return "login";
        }
    }
}
