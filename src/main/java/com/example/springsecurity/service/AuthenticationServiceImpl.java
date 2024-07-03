package com.example.springsecurity.service;

import com.example.springsecurity.config.security.JWTTokenProvider;
import com.example.springsecurity.entity.InvaliedToken;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.AppException;
import com.example.springsecurity.exception.ErrorCode;
import com.example.springsecurity.payload.request.ExchangeTokenRequest;
import com.example.springsecurity.payload.request.InvalidTokenRequest;
import com.example.springsecurity.payload.response.AuthenticationResponse;
import com.example.springsecurity.repository.InvalidTokenRepository;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.httpClient.OutboundIdentityClient;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.repository.httpClient.OutboundUserClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    JWTTokenProvider jwtTokenProvider;
    InvalidTokenRepository invalidTokenRepository;
    UserRepository userRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    RoleRepository roleRepository;
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected  String CLIENT_ID;
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    protected  String CLIENT_SECRET;
    // Sau khi thanh công thì sẽ trả về trang này
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    protected  String REDIRECT_URI ;
    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    @Override
    public AuthenticationResponse outboundAuthenticate(String code) {
        log.info("1");
        // Exchange token lấy điwpkc cáo accessToken
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());
        log.info("Token Response {}", response.getAccessToken());
        var userInfo = outboundUserClient.getUserInfo("json", response.getAccessToken());
        log.info("User info {}" , userInfo);
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        var user = userRepository.findByUsername(userInfo.getEmail()).orElseGet(
                () -> userRepository.save(User.builder()
                        .username(userInfo.getEmail())
                        .email(userInfo.getEmail())
                        .roles(List.of(userRole))
                        .build()));

        return AuthenticationResponse.builder().token(response.getAccessToken()).build();
    }

    @Override
    public void logout(InvalidTokenRequest invalidTokenRequest) {
        try {
            if (jwtTokenProvider.validateToken(invalidTokenRequest.getToken())) {
                String jti = jwtTokenProvider.extractIdToken(invalidTokenRequest.getToken());
                Date dateExpire = jwtTokenProvider.extractExpiration(invalidTokenRequest.getToken());
                InvaliedToken invaliedToken = InvaliedToken.builder().id(jti).dateExpried(dateExpire).build();
                invalidTokenRepository.save(invaliedToken);
            }
            ;
            log.info("authuzition");
        } catch (AppException e) {
            log.info("Token already exists");
        }
    }

    @Override
    public AuthenticationResponse refreshToken(InvalidTokenRequest invalidTokenRequest) {
        if (invalidAndSave(invalidTokenRequest.getToken())) {
            String username = jwtTokenProvider.extractUsername(invalidTokenRequest.getToken());
            User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.INVALID_USER));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            String token = jwtTokenProvider.genreToken(authentication);
            return AuthenticationResponse.builder().token(token).build();
        }
        return AuthenticationResponse.builder().token(null).build();
    }


    private boolean invalidAndSave(String token) {
        try {
            if (jwtTokenProvider.validateToken(token)) {
                String jti = jwtTokenProvider.extractIdToken(token);
                Date dateExpire = jwtTokenProvider.extractExpiration(token);
                InvaliedToken invaliedToken = InvaliedToken.builder().id(jti).dateExpried(dateExpire).build();
                invalidTokenRepository.save(invaliedToken);
                return true;
            }
        } catch (AppException e) {
            log.info("Token invalid");
        }
        return false;
    }
}
