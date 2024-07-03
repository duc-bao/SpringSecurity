package com.example.springsecurity.service;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.APIResponse;
import com.example.springsecurity.payload.request.InvalidTokenRequest;
import com.example.springsecurity.payload.response.AuthenticationResponse;
import com.example.springsecurity.payload.response.UserResponse;
import org.springframework.http.ResponseEntity;

import com.example.springsecurity.payload.request.SignupRequest;

public interface UserService {
    UserResponse register(SignupRequest signupRequest);
}
