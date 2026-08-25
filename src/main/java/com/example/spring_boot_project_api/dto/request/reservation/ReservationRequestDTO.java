package com.example.spring_boot_project_api.dto.request.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReservationRequestDTO {
  @NotNull
  private Long vehicleId;

  @NotNull
  private Long pickUpLocationId;

  @NotNull
  private Long returnLocationId;

  @NotNull
  private LocalDateTime pickUpDateTime;

  @NotNull
  private LocalDateTime returnDateTime;

  private BigDecimal depositAmount;

  private BigDecimal discountAmount;

  private BigDecimal additionalCharges;

  @Size(max = 255)
  private String notes;
}
