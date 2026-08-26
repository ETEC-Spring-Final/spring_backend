package com.example.spring_boot_project_api.dto.response.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.RentalStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalResponseDTO {
  private Long id;
  private Long reservationId;
  private Long vehicleId;
  private Long userId;
  private Long pickUpLocationId;
  private Long returnLocationId;
  private LocalDateTime pickUpDateTime;
  private LocalDateTime expectedReturnDateTime;
  private LocalDateTime actualReturnDateTime;
  private RentalStatusEnum status;
  private BigDecimal basePrice;
  private BigDecimal discountAmount;
  private BigDecimal additionalCharges;
  private BigDecimal lateFee;
  private BigDecimal totalPrice;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
