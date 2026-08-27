package com.example.spring_boot_project_api.dto.response.discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.DiscountTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscountResponseDTO {
  private Long id;
  private String code;
  private String description;
  private DiscountTypeEnum type;
  private BigDecimal value;
  private LocalDateTime validFrom;
  private LocalDateTime validTo;
  private Integer maxUses;
  private Integer usedCount;
  private Boolean isActive;
}
