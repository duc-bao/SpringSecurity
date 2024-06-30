package com.example.springsecurity.payload.request;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.validator.DobContrains;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "INVALID_USER")
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @DobContrains(min = 18)
    @NotNull
    private LocalDate dob;
    private List<String> roles;
}
