package com.example.spring_boot_project_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.user.TelegramLoginRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.service.TelegramAuthService;

import jakarta.validation.Valid;

/**
 * Sits under /api/auth, which SecurityConfig already permitAll()s via
 * .requestMatchers("/api/auth/**").permitAll() — no SecurityConfig change
 * needed for this endpoint.
 */
@RestController
@RequestMapping("/api/auth")
public class TelegramAuthController {

  @Autowired
  private TelegramAuthService telegramAuthService;

  @PostMapping("/telegram")
  public AuthResponseDTO telegramLogin(@Valid @RequestBody TelegramLoginRequestDTO dto) {
    return telegramAuthService.loginOrRegister(dto);
  }
}