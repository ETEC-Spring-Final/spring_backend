package com.example.spring_boot_project_api.service;

import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;

public interface UserService {
  AuthResponseDTO register(RegisterRequestDTO dto);

  AuthResponseDTO login(LoginRequestDTO dto);
}
