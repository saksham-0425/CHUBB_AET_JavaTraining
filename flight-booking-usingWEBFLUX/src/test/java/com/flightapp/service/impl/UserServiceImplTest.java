package com.flightapp.service.impl;

import com.flightapp.dto.request.LoginRequest;
import com.flightapp.dto.request.RegisterRequest;
import com.flightapp.model.AppUser;
import com.flightapp.repository.AppUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------
    //  REGISTER USER TEST
    // ---------------------------
    @Test
    void testRegisterNewUser() {

        RegisterRequest request = new RegisterRequest();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("pass");

        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(Mono.just(false));

        when(userRepository.save(any()))
                .thenReturn(Mono.just(new AppUser()));

        StepVerifier.create(userService.register(request))
                .verifyComplete();

        verify(userRepository, times(1)).save(any());
    }

    // ---------------------------
    //  LOGIN USER TEST
    // ---------------------------
    @Test
    void testLoginValidUser() {

        AppUser user = new AppUser();
        user.setId(1L);
        user.setEmail("john@gmail.com");
        user.setPasswordHash("pass");

        LoginRequest req = new LoginRequest();
        req.setEmail("john@gmail.com");
        req.setPassword("pass");

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userService.login(req))
                .assertNext(u -> {
                    assertEquals("john@gmail.com", u.getEmail());
                })
                .verifyComplete();
    }
}
