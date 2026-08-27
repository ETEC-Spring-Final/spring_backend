package com.example.spring_boot_project_api.dto.response.reservation_service;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationServicesResponseDTO {
  private Long id;
  private Long reservationId;
  private Long serviceId;
  private Integer quantity;
  private BigDecimal priceAtBooking;
}
