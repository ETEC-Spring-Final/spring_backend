package com.example.spring_boot_project_api.dto.request.reservation_service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationServicesRequestDTO {
  @NotNull
  private Long reservationId;

  @NotNull
  private Long serviceId;

  @NotNull
  @Min(1)
  private Integer quantity;
}
