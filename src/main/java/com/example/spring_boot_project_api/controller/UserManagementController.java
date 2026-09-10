package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.response.user.UserResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {
  @Autowired
  private UserService userService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public List<UserResponseDTO> getAllUsers() {
    return userService.getAllUsers();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public UserResponseDTO getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/role")
  public UserResponseDTO updateUserRole(@PathVariable Long id, @RequestParam RoleEnum role) {
    return userService.updateUserRole(id, role);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping("/{id}/active")
  public UserResponseDTO setUserActive(@PathVariable Long id, @RequestParam boolean active) {
    return userService.setUserActive(id, active);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }
}