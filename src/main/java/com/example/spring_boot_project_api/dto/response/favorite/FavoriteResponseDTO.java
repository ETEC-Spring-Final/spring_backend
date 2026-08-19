package com.example.spring_boot_project_api.dto.response.favorite;

import java.time.LocalDateTime;

import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteResponseDTO {
  private Long id;
  private VehicleResponseDTO vehicle;
  private LocalDateTime createdAt;
}
