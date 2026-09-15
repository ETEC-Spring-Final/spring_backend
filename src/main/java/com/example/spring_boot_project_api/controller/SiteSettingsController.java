package com.example.spring_boot_project_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.settings.SiteSettingsRequestDTO;
import com.example.spring_boot_project_api.dto.response.settings.SiteSettingsResponseDTO;
import com.example.spring_boot_project_api.service.SiteSettingsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SiteSettingsController {

  private final SiteSettingsService siteSettingsService;

  // Public and unauthenticated on purpose: the login page, the public
  // marketing site header/footer, and the admin dashboard sidebar/header
  // all need the logo + site name BEFORE (or without) a JWT existing.
  // Registered as permitAll in SecurityConfig.
  @GetMapping
  public SiteSettingsResponseDTO getSettings() {
    return siteSettingsService.getSettings();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping
  public SiteSettingsResponseDTO updateSettings(@Valid @RequestBody SiteSettingsRequestDTO dto) {
    return siteSettingsService.updateSettings(dto);
  }
}