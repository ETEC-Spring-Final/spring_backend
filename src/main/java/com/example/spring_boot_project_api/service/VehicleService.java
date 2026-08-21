package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.vehicle.VehicleRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;

public interface VehicleService {
  VehicleResponseDTO createVehicle(VehicleRequestDTO dto);

  VehicleResponseDTO getVehicleById(Long id);

  List<VehicleResponseDTO> getAllVehicles();

  VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto);

  void deleteVehicle(Long id);
}
