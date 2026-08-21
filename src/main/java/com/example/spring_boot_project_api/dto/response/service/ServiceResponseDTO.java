package com.example.spring_boot_project_api.dto.response.service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponseDTO {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Boolean isActive;
}
