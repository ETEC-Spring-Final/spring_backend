package com.example.spring_boot_project_api.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

  @NotBlank(message = "Current password is required")
  private String currentPassword;

  @NotBlank(message = "New password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8-100 characters")
  private String newPassword;

  // Not read by UserServiceImpl (server only trusts newPassword) — kept so the
  // frontend can send it and so @NotBlank still catches an empty confirm field
  // if you later add cross-field validation.
  @NotBlank(message = "Please confirm your new password")
  private String confirmPassword;
}