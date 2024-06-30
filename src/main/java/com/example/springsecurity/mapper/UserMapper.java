package com.example.springsecurity.mapper;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.payload.request.SignupRequest;
import com.example.springsecurity.payload.response.UserResponse;
import com.example.springsecurity.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(ignore = true, target = "roles")
    User toUser(SignupRequest signupRequest);

    UserResponse toUserResponse(User user);
    @Mapping(ignore = true, target = "roles")
    void updateUser(@MappingTarget User user, SignupRequest signupRequest);

}
