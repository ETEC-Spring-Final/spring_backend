package com.example.spring_boot_project_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
