package com.example.spring_boot_project_api.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.password_reset_token.ForgotPasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.password_reset_token.ResetPasswordRequestDTO;
import com.example.spring_boot_project_api.model.PasswordResetToken;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.PasswordResetRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.PasswordResetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
  private final PasswordResetRepository passwordResetRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public String requestPasswordReset(ForgotPasswordRequestDTO dto) {
    User user = userRepository.findByEmail(dto.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));

    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setUser(user);
    resetToken.setToken(token);
    resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

    passwordResetRepository.save(resetToken);

    return token; // TEMP: returned directly since email isn't configured
  }

  @Override
  public void resetPassword(ResetPasswordRequestDTO dto) {
    PasswordResetToken resetToken = passwordResetRepository.findByToken(dto.getToken())
        .orElseThrow(() -> new RuntimeException("Invalid token"));

    if (resetToken.getUsedAt() != null) {
      throw new RuntimeException("This token has already been used");
    }

    if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new RuntimeException("Token has expired");
    }

    User user = resetToken.getUser();
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);

    resetToken.setUsedAt(LocalDateTime.now());
    passwordResetRepository.save(resetToken);
  }
}
