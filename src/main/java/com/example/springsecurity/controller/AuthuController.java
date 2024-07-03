package com.example.springsecurity.controller;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.APIResponse;
import com.example.springsecurity.payload.request.InvalidTokenRequest;
import com.example.springsecurity.payload.response.AuthenticationResponse;
import com.example.springsecurity.payload.response.UserResponse;
import com.example.springsecurity.service.AuthenticationService;
import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.example.springsecurity.config.security.JWTTokenProvider;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthuController {
    UserService userService;
    AuthenticationService authenticationService;
    @PostMapping("/outbound/authentication")
    public APIResponse<AuthenticationResponse> outboundAuthentication(@RequestParam(value = "code") String code){
        log.info( "String code: {}",code);
        var result = authenticationService.outboundAuthenticate(code);
        return APIResponse.<AuthenticationResponse>builder().result(result).build();
    }
//    Logger logger = LoggerFactory.getLogger(AuthuController.class);
    //    @PostMapping("/login")
    //    public String loginPage(@RequestBody LoginDTO loginDTO){
    //        Authentication authentication = authenticationManager.authenticate(new
    // UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
    //        if(authentication.isAuthenticated()){
    //            final String jwt = jwtTokenProvider.genreToken(authentication);
    //            return  jwt;
    //        }
    //        return  "Loi";
    //    }
    @PostMapping("/register")
    public APIResponse<UserResponse> register(@Valid @RequestBody SignupRequest signupRequest) {
       return APIResponse.<UserResponse>builder().result(userService.register(signupRequest)).build();
    }
    @PostMapping("/logout")
    public APIResponse<Void> logout(@RequestBody InvalidTokenRequest invalidTokenRequest){
        authenticationService.logout(invalidTokenRequest);
        return APIResponse.<Void>builder().build();
    }
    @PostMapping("/refresh")
    public APIResponse<AuthenticationResponse> refreshToken(@RequestBody InvalidTokenRequest invalidTokenRequest){
        var result = authenticationService.refreshToken(invalidTokenRequest);
        return APIResponse.<AuthenticationResponse>builder().result(result).build();
    }
    @GetMapping("/hello")
    public String hello() {
        return "HelloWorld";
    }

    @GetMapping("/admin")
    //    @PreAuthorize("hasAuthority('ADMIN')")
    public String helloAdmin() {
        return "ADMIN";
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String helloUser() {
        return "User";
    }
}
