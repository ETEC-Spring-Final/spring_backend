package com.example.spring_boot_project_api.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequestDTO {
  @NotBlank
  private String name;
}
