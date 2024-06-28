package com.example.springsecurity.service;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.payload.request.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> register(SignupRequest signupRequest);
}
