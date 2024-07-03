package com.example.springsecurity.service;

import com.example.springsecurity.payload.request.InvalidTokenRequest;
import com.example.springsecurity.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    void logout(InvalidTokenRequest invalidTokenRequest);
    AuthenticationResponse refreshToken(InvalidTokenRequest invalidTokenRequest);
    AuthenticationResponse outboundAuthenticate(String code);
}
