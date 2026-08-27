package com.example.spring_boot_project_api.dto.request.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.DiscountTypeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiscountRequestDTO {
  @NotBlank
  private String code;

  @NotBlank
  @Size(max = 255)
  private String description;

  @NotNull
  private DiscountTypeEnum type;

  @NotNull
  private BigDecimal value;

  @NotNull
  private LocalDateTime validFrom;

  private LocalDateTime validTo;

  private Integer maxUses;

  private Boolean isActive;
}
