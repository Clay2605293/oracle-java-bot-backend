package com.oraclejavabot.features.auth.controller;

import com.oraclejavabot.features.auth.dto.LoginRequestDTO;
import com.oraclejavabot.features.auth.dto.LoginResponseDTO;
import com.oraclejavabot.features.auth.dto.RegisterRequestDTO;
import com.oraclejavabot.features.auth.service.AuthService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequestDTO request){
        authService.register(request);
    }
}