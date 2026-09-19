package com.example.spring_boot_project_api.dto.response.user;

import com.example.spring_boot_project_api.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
  private Long id;
  private String email;
  private RoleEnum role;
  private String token;
}
