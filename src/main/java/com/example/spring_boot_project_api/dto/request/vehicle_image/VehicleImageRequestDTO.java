package com.example.spring_boot_project_api.dto.request.vehicle_image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleImageRequestDTO {
  @NotNull
  private Long vehicleId;

  @NotBlank
  @Size(max = 255)
  private String imageUrl;

  private Boolean isPrimary;

  private Integer displayOrder;
}
