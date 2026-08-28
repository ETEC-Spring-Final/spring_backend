package com.example.spring_boot_project_api.service;

import com.example.spring_boot_project_api.dto.request.password_reset_token.ForgotPasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.password_reset_token.ResetPasswordRequestDTO;

public interface PasswordResetService {
  String requestPasswordReset(ForgotPasswordRequestDTO dto);

  void resetPassword(ResetPasswordRequestDTO dto);
}
