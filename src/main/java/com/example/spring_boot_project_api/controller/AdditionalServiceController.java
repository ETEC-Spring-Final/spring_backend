package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.additional_service.AdditionalServiceResponseDTO;
import com.example.spring_boot_project_api.service.AdditionalServiceService;

import lombok.RequiredArgsConstructor;

/**
 * Per-day rental add-ons (extra driver, GPS, child seat, full insurance).
 * Read is public (SecurityConfig permits GET /api/additional-services/**)
 * so the booking form can list them without a JWT. Kept separate from the
 * Services catalogue on purpose — those use flat pricing and stay
 * admin-managed.
 */
@RestController
@RequestMapping("/api/additional-services")
@RequiredArgsConstructor
public class AdditionalServiceController {
  private final AdditionalServiceService additionalServiceService;

  @GetMapping
  public List<AdditionalServiceResponseDTO> getActiveServices() {
    return additionalServiceService.getActiveServices();
  }
}