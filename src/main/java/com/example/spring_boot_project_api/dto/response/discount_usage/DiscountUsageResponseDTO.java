package com.example.spring_boot_project_api.dto.response.discount_usage;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiscountUsageResponseDTO {
  private Long id;
  private Long discountId;
  private Long userId;
  private Long reservationId;
  private LocalDateTime usedAt;
}
