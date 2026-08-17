package com.example.spring_boot_project_api.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
  private Long id;
  private String email;
  private String role;
  private String token;
}
