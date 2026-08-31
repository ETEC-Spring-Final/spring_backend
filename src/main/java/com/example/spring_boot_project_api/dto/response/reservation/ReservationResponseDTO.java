package com.example.spring_boot_project_api.dto.response.reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.ReservationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResponseDTO {
  private Long id;
  private Long userId;
  private Long vehicleId;
  private Long pickUpLocationId;
  private Long returnLocationId;
  private LocalDateTime pickUpDateTime;
  private LocalDateTime returnDateTime;
  private ReservationStatusEnum status;
  private BigDecimal totalPrice;
  private BigDecimal depositAmount;
  private BigDecimal discountAmount;
  private BigDecimal additionalCharges;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
