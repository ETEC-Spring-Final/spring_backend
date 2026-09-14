package com.example.spring_boot_project_api.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name must be under 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name must be under 50 characters")
  private String lastName;

  @NotBlank(message = "Phone is required")
  @Size(min = 9, max = 10, message = "Phone number is only 9-10 digits")
  private String phone;

  // Optional: URL or base64 string of the uploaded picture. Nullable/blank is
  // allowed — UserServiceImpl only updates it when non-blank.
  private String profilePicture;
}