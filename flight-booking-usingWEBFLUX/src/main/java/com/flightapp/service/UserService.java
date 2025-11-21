package com.flightapp.service;

import com.flightapp.dto.request.LoginRequest;
import com.flightapp.dto.request.RegisterRequest;
import com.flightapp.model.AppUser;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Void> register(RegisterRequest request);

    Mono<AppUser> login(LoginRequest request);
}
