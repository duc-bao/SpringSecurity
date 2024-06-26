package com.example.springsecurity.controller;

import com.example.springsecurity.payload.LoginDTO;
import com.example.springsecurity.security.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthuController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @PostMapping("/login")
    public String loginPage(@RequestBody LoginDTO loginDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        if(authentication.isAuthenticated()){
            final String jwt = jwtTokenProvider.genreToken(authentication);
            return  jwt;
        }
        return  "Loi";
    }


    @GetMapping("/hello")
    public String hello(){
        return  "HelloWorld";
    }
    @GetMapping("/admin")
//    @PreAuthorize("hasAuthority('ADMIN')")
    public String helloAdmin(){
        return "ADMIN";
    }
    @GetMapping("/user")
  @PreAuthorize("hasAuthority('ROLE_USER')")
    public String helloUser(){
        return "User";
    }
}
