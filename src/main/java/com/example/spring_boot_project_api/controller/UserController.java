package com.example.spring_boot_project_api.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.password_reset_token.ForgotPasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.password_reset_token.ResetPasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.dto.response.user.LogoutResponseDTO;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.LoginHistoryService;
import com.example.spring_boot_project_api.service.PasswordResetService;
import com.example.spring_boot_project_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private PasswordResetService passwordResetService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LoginHistoryService loginHistoryService;

  @PostMapping("/register")
  public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
    return userService.register(dto);
  }

  @PostMapping("/login")
  public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
    return userService.login(dto);
  }

  @PostMapping("/logout")
  public LogoutResponseDTO logout(Principal principal) {
    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }

    User user = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));

    loginHistoryService.recordLogout(user.getId());

    return LogoutResponseDTO.builder()
        .message("Successfully logged out")
        .build();
  }

  @PostMapping("/forgot-password")
  public String forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
    return passwordResetService.requestPasswordReset(dto);
  }

  @PostMapping("/reset-password")
  public void resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
    passwordResetService.resetPassword(dto);
  }
}