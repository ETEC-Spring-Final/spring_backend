package com.example.spring_boot_project_api.dto.request.user;

import com.example.spring_boot_project_api.enums.GenderEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name must be under 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name must be under 50 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 50, message = "Email must be under 50 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 10, message = "Password must be between 8-100 characters")
  private String password;

  @NotBlank(message = "Phone number is required")
  @Size(min = 9, max = 10, message = "Phone number is only 9-10 digits")
  private String phone;

  @NotNull(message = "Gender is required")
  private GenderEnum gender;
}
