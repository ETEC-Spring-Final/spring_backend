package com.example.spring_boot_project_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {
  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
    return userService.register(dto);
  }

  @PostMapping("/login")
  public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
    return userService.login(dto);
  }
}
