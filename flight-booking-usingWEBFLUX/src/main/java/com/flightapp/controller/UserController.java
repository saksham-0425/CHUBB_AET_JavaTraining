package com.flightapp.controller;

import com.flightapp.dto.request.RegisterRequest;
import com.flightapp.dto.request.LoginRequest;
import com.flightapp.model.AppUser;
import com.flightapp.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<String> register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request)
                .thenReturn("User registered successfully");
    }

    @PostMapping("/login")
    public Mono<String> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request)
                .map(user -> "Login successful for: " + user.getEmail());
    }
}
