package com.example.spring_boot_project_api.dto.request.discount_usage;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiscountUsageRequestDTO {
  @NotNull
  private Long discountId;

  @NotNull
  private Long reservationId;
}
