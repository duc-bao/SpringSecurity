package com.example.springsecurity.service;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.payload.response.MessageResponse;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public ResponseEntity<?> register(SignupRequest signupRequest) {
        if(userRepository.existsByUsername(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already exists"));
        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already exists"));
        }

        User user = new User().builder().username(signupRequest.getUsername()).email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword())).build();

        Set<String> roleSet = signupRequest.getRoles();
        Set<Role>   roles = new HashSet<>();
        if(roleSet == null){
            Role role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(role);
        }else {
            roleSet.forEach(role ->{
                switch (role){
                    case "ROLE_ADMIN":
                        Role adminrole = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("ROLE is not found."));
                        roles.add(adminrole);
                    default:
                        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() ->new RuntimeException("Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User resgiter success"));
    }
}
