package com.example.spring_boot_project_api.controller;

import java.security.Principal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.user.LoginHistoryResponseDTO;
import com.example.spring_boot_project_api.service.LoginHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {
  @Autowired
  private LoginHistoryService loginHistoryService;

  @GetMapping("/me/login-history")
  public Page<LoginHistoryResponseDTO> getMyLoginHistory(
      Principal principal,
      @ParameterObject @PageableDefault(size = 8, sort = "loggedInAt", direction = Sort.Direction.DESC) Pageable pageable) {

    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }

    return loginHistoryService.getUserLoginHistoryByEmail(principal.getName(), pageable);
  }
}