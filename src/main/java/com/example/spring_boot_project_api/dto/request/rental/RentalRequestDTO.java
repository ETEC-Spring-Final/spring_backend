package com.example.spring_boot_project_api.dto.request.rental;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RentalRequestDTO {
  @NotNull
  private Long reservationId;

  private BigDecimal discountAmount;

  private BigDecimal additionalCharges;

  @Size(max = 255)
  private String notes;
}
