package com.example.spring_boot_project_api.dto.request.vehicle_image;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleImageRequestDTO {
  @NotNull
  private Long vehicleId;

  @NotNull
  private Long attachmentId;

  private Boolean isPrimary;

  private Integer displayOrder;
}
