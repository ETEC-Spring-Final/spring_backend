package com.example.spring_boot_project_api.dto.response.vehicle_image;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleImageResponseDTO {
  private Long id;
  private Long vehicleId;
  private String imageUrl;
  private Boolean isPrimary;
  private Integer displayOrder;
  private LocalDateTime createdAt;
}
