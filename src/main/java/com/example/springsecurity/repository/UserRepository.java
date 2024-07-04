package com.example.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.springsecurity.entity.User;

import java.util.Optional;

//@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(String username);

    public User findByEmail(String email);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);
}
