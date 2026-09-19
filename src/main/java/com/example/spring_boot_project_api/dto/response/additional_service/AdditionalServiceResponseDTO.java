package com.example.spring_boot_project_api.dto.response.additional_service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdditionalServiceResponseDTO {
  private Long id;
  private String name;
  private String nameKh;
  private String description;
  private BigDecimal pricePerDay;
  private String icon;
  private Boolean active;
}