package com.example.spring_boot_project_api.controller;

import java.security.Principal;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.user.ChangePasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.UpdateProfileRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.LoginHistoryResponseDTO;
import com.example.spring_boot_project_api.dto.response.user.UserResponseDTO;
import com.example.spring_boot_project_api.service.LoginHistoryService;
import com.example.spring_boot_project_api.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

  @Autowired
  private LoginHistoryService loginHistoryService;

  private final UserService userService;

  @GetMapping("/me/login-history")
  public Page<LoginHistoryResponseDTO> getMyLoginHistory(
      Principal principal,
      @ParameterObject @PageableDefault(size = 8, sort = "loggedInAt", direction = Sort.Direction.DESC) Pageable pageable) {

    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }

    return loginHistoryService.getUserLoginHistoryByEmail(principal.getName(), pageable);
  }

  @GetMapping("/me")
  public UserResponseDTO getMyProfile(Principal principal) {
    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }
    return userService.getMyProfile(principal.getName());
  }

  @PutMapping("/me")
  public UserResponseDTO updateMyProfile(
      Principal principal,
      @Valid @RequestBody UpdateProfileRequestDTO dto) {

    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }
    return userService.updateMyProfile(principal.getName(), dto);
  }

  @PostMapping("/me/change-password")
  public void changePassword(
      Principal principal,
      @Valid @RequestBody ChangePasswordRequestDTO dto) {

    if (principal == null) {
      throw new RuntimeException("Unauthorized");
    }
    userService.changePassword(principal.getName(), dto);
  }
}