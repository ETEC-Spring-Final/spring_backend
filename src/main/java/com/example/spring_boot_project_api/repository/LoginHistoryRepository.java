package com.example.spring_boot_project_api.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring_boot_project_api.model.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

  Page<LoginHistory> findByUserIdOrderByLoggedInAtDesc(Long userId, Pageable pageable);

  Page<LoginHistory> findByAttemptedUsernameContainingIgnoreCaseOrderByLoggedInAtDesc(String email, Pageable pageable);

  Optional<LoginHistory> findTopByUserIdAndSuccessTrueAndLoggedOutAtIsNullOrderByLoggedInAtDesc(Long userId);
}