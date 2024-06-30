package com.example.springsecurity.controller;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.APIResponse;
import com.example.springsecurity.payload.response.UserResponse;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.springsecurity.config.security.JWTTokenProvider;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.service.UserService;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthuController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

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
