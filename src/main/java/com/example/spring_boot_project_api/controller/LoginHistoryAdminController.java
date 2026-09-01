package com.example.spring_boot_project_api.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.user.LoginHistoryResponseDTO;
import com.example.spring_boot_project_api.service.LoginHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/login-history")
@RequiredArgsConstructor
public class LoginHistoryAdminController {

  private final LoginHistoryService loginHistoryService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public Page<LoginHistoryResponseDTO> getAllLoginHistory(
      @RequestParam(required = false) String email,
      @ParameterObject @PageableDefault(size = 20, sort = "loggedInAt", direction = Sort.Direction.DESC) Pageable pageable) {

    return loginHistoryService.getAllLoginHistories(email, pageable);
  }
}