package com.example.spring_boot_project_api.dto.response.brand;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandResponseDTO {
  private Long id;
  private String name;
}
