package com.example.springsecurity.service;

import org.springframework.http.ResponseEntity;

import com.example.springsecurity.payload.request.SignupRequest;

public interface UserService {
    ResponseEntity<?> register(SignupRequest signupRequest);
}
