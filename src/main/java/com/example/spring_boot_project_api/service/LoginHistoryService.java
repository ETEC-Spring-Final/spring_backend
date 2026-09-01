package com.example.spring_boot_project_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.spring_boot_project_api.dto.response.user.LoginHistoryResponseDTO;

public interface LoginHistoryService {

  void recordLoginAttempt(String email, boolean success, String ipAddress, String device);

  void recordLogout(Long userId);

  Page<LoginHistoryResponseDTO> getUserLoginHistory(Long userId, Pageable pageable);

  Page<LoginHistoryResponseDTO> getUserLoginHistoryByEmail(String email, Pageable pageable);

  // Admin/Manager method to view all login attempts (optionally filtered by
  // email)
  Page<LoginHistoryResponseDTO> getAllLoginHistories(String filterEmail, Pageable pageable);
}