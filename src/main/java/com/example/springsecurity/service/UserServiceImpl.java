package com.example.springsecurity.service;

import java.util.*;

import com.example.springsecurity.config.security.JWTTokenProvider;
import com.example.springsecurity.entity.InvaliedToken;
import com.example.springsecurity.exception.APIResponse;
import com.example.springsecurity.exception.AppException;
import com.example.springsecurity.exception.ErrorCode;
import com.example.springsecurity.mapper.UserMapper;
import com.example.springsecurity.payload.request.InvalidTokenRequest;
import com.example.springsecurity.payload.response.AuthenticationResponse;
import com.example.springsecurity.payload.response.UserResponse;
import com.example.springsecurity.repository.InvalidTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.payload.response.MessageResponse;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.UserRepository;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private InvalidTokenRepository invalidTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Override
    @Transactional
    public UserResponse register(SignupRequest signupRequest) {
            User user = userMapper.toUser(signupRequest);
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            List<String> roleSet = signupRequest.getRoles();
            List<Role> roles = new ArrayList<>();
            if (roleSet == null) {
                Role role = roleRepository
                        .findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(role);
            } else {
                roleSet.forEach(role -> {
                    switch (role) {
                        case "ROLE_ADMIN":
                            Role adminrole = roleRepository
                                    .findByName("ROLE_ADMIN")
                                    .orElseThrow(() -> new RuntimeException("ROLE is not found."));
                            roles.add(adminrole);
                        default:
                            Role userRole = roleRepository
                                    .findByName("ROLE_USER")
                                    .orElseThrow(() -> new RuntimeException("Role is not found"));
                            roles.add(userRole);
                    }
                });
            }
            user.setRoles(roles);
            try {
                user = userRepository.save(user);
            }catch (DataIntegrityViolationException e){
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            return userMapper.toUserResponse(user);
    }

    @Override
    public void logout(InvalidTokenRequest invalidTokenRequest) {
        try {
            if(jwtTokenProvider.validateToken(invalidTokenRequest.getToken())){
                String jti = jwtTokenProvider.extractIdToken(invalidTokenRequest.getToken());
                Date dateExpire = jwtTokenProvider.extractExpiration(invalidTokenRequest.getToken());
                InvaliedToken invaliedToken = InvaliedToken.builder().id(jti).dateExpried(dateExpire).build();
                invalidTokenRepository.save(invaliedToken);
            }
            ;log.info("authuzition");
        }catch (AppException e){
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
            return  AuthenticationResponse.builder().token(null).build();
    }
    private boolean invalidAndSave(String token){
        try {
            if(jwtTokenProvider.validateToken(token)){
                String jti = jwtTokenProvider.extractIdToken(token);
                Date dateExpire = jwtTokenProvider.extractExpiration(token);
                InvaliedToken invaliedToken = InvaliedToken.builder().id(jti).dateExpried(dateExpire).build();
                invalidTokenRepository.save(invaliedToken);
                return  true;
            }
        }catch (AppException e){
            log.info("Token invalid");
        }
        return  false;
    }
}
