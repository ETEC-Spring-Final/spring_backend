package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.user.ChangePasswordRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.LoginRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.RegisterRequestDTO;
import com.example.spring_boot_project_api.dto.request.user.UpdateProfileRequestDTO;
import com.example.spring_boot_project_api.dto.response.user.AuthResponseDTO;
import com.example.spring_boot_project_api.dto.response.user.UserResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;

public interface UserService {
  AuthResponseDTO register(RegisterRequestDTO dto);
  AuthResponseDTO login(LoginRequestDTO dto);

  // ↓ existing methods for Customer/User management
  List<UserResponseDTO> getAllUsers();
  UserResponseDTO getUserById(Long id);
  UserResponseDTO updateUserRole(Long id, RoleEnum role);
  UserResponseDTO setUserActive(Long id, boolean active);
  void deleteUser(Long id);

  // ↓ new methods for "My Profile" self-service
  UserResponseDTO getMyProfile(String email);
  UserResponseDTO updateMyProfile(String email, UpdateProfileRequestDTO dto);
  void changePassword(String email, ChangePasswordRequestDTO dto);
}