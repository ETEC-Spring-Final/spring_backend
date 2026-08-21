package com.example.spring_boot_project_api.dto.request.service;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceRequestDTO {
  @Size(max = 100)
  private String name;

  @Size(max = 255)
  private String description;

  @NotNull
  private BigDecimal price;

  @NotNull
  private Boolean isActive;
}
