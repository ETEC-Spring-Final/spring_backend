package com.example.spring_boot_project_api.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
}
