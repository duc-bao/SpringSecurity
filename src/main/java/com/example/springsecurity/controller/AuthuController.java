package com.example.springsecurity.controller;

import com.example.springsecurity.config.security.JWTTokenProvider;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthuController {

    @Autowired
    private UserService userService;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    Logger logger = LoggerFactory.getLogger(AuthuController.class);
//    @PostMapping("/login")
//    public String loginPage(@RequestBody LoginDTO loginDTO){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
//        if(authentication.isAuthenticated()){
//            final String jwt = jwtTokenProvider.genreToken(authentication);
//            return  jwt;
//        }
//        return  "Loi";
//    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest){
        try {
            logger.info("Register success");
            return  userService.register(signupRequest);
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
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
