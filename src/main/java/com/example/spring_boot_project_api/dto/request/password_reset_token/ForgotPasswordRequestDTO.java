package com.example.spring_boot_project_api.dto.request.password_reset_token;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {

  @NotBlank
  @Email
  private String email;
}
