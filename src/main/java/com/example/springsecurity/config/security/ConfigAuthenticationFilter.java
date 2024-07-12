package com.example.springsecurity.config.security;

import java.io.IOException;

import com.example.springsecurity.exception.APIResponse;
import com.example.springsecurity.payload.response.AuthenticationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.springsecurity.payload.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class ConfigAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    public ConfigAuthenticationFilter(AuthenticationManager authentication) {
        super(new AntPathRequestMatcher("/login"));
        setAuthenticationManager(authentication);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        System.out.println(loginRequest);
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        String token = jwtTokenProvider.genreToken(authResult);
        String refrshToken = jwtTokenProvider.generRefreshToken(authResult);
        log.trace("Token response {}", token);
        log.trace("Refresh token{}", refrshToken);
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refrshToken)
                .build();
        APIResponse<AuthenticationResponse> apiResponse = APIResponse.<AuthenticationResponse>builder()
                .result(authResponse)
                .build();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication Failed: " + failed.getMessage());
    }
}
