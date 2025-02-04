package com.example.springsecurity.repository;

import com.example.springsecurity.entity.InvaliedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvaliedToken, String> {
}
