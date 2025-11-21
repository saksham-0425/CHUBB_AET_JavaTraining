package com.flightapp.service.impl;

import com.flightapp.dto.request.LoginRequest;
import com.flightapp.dto.request.RegisterRequest;
import com.flightapp.model.AppUser;
import com.flightapp.repository.AppUserRepository;
import com.flightapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    @Override
    public Mono<Void> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Email already registered"));
                    }
                    AppUser user = new AppUser();
                    user.setName(request.getName());
                    user.setEmail(request.getEmail());
                    // TODO: hash password, e.g. BCrypt
                    user.setPasswordHash(request.getPassword());
                    return userRepository.save(user).then();
                });
    }

    @Override
    public Mono<AppUser> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid email or password")))
                .flatMap(user -> {
                    if (!user.getPasswordHash().equals(request.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid email or password"));
                    }
                    return Mono.just(user);
                });
    }
}
