package com.example.spring_boot_project_api.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_boot_project_api.dto.response.user.LoginHistoryResponseDTO;
import com.example.spring_boot_project_api.model.LoginHistory;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.LoginHistoryRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.LoginHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

  private final UserRepository userRepository;
  private final LoginHistoryRepository loginHistoryRepository;

  @Override
  @Transactional
  public void recordLoginAttempt(String email, boolean success, String ipAddress, String device) {
    User user = userRepository.findByEmail(email).orElse(null);

    LoginHistory history = LoginHistory.builder()
        .user(user)
        .attemptedUsername(email)
        .ipAddress(ipAddress)
        .device(device)
        .success(success)
        .build();

    loginHistoryRepository.save(history);
  }

  @Override
  @Transactional
  public void recordLogout(Long userId) {
    loginHistoryRepository
        .findTopByUserIdAndSuccessTrueAndLoggedOutAtIsNullOrderByLoggedInAtDesc(userId)
        .ifPresent(history -> {
          history.setLoggedOutAt(LocalDateTime.now());
          loginHistoryRepository.save(history);
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LoginHistoryResponseDTO> getUserLoginHistory(Long userId, Pageable pageable) {
    return loginHistoryRepository.findByUserIdOrderByLoggedInAtDesc(userId, pageable)
        .map(this::mapToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LoginHistoryResponseDTO> getUserLoginHistoryByEmail(String email, Pageable pageable) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return getUserLoginHistory(user.getId(), pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LoginHistoryResponseDTO> getAllLoginHistories(String filterEmail, Pageable pageable) {
    Page<LoginHistory> historyPage;

    if (filterEmail != null && !filterEmail.isBlank()) {
      historyPage = loginHistoryRepository.findByAttemptedUsernameContainingIgnoreCaseOrderByLoggedInAtDesc(filterEmail,
          pageable);
    } else {
      historyPage = loginHistoryRepository.findAll(pageable);
    }

    return historyPage.map(this::mapToDTO);
  }

  private LoginHistoryResponseDTO mapToDTO(LoginHistory history) {
    return LoginHistoryResponseDTO.builder()
        .id(history.getId())
        .attemptedUsername(history.getAttemptedUsername())
        .ipAddress(history.getIpAddress())
        .device(history.getDevice())
        .success(history.getSuccess())
        .loggedInAt(history.getLoggedInAt())
        .loggedOutAt(history.getLoggedOutAt())
        .build();
  }
}