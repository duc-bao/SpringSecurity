package com.example.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthuController {

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
