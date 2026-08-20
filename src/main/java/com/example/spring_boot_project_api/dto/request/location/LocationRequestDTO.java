package com.example.spring_boot_project_api.dto.request.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocationRequestDTO {
  @NotBlank(message = "Garage name is required")
  @Size(max = 100)
  private String name;

  @NotBlank(message = "Address is required")
  @Size(max = 255)
  private String address;

  @NotBlank(message = "City is required")
  @Size(max = 100)
  private String city;

  @NotBlank(message = "Phone number is required")
  @Size(min = 9, max = 10)
  private String phone;

  @NotNull(message = "Active is required")
  private Boolean isActive;
}
