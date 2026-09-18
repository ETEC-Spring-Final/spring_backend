package com.example.spring_boot_project_api.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.spring_boot_project_api.dto.request.vehicle.VehicleRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;
import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.FuelTypeEnum;
import com.example.spring_boot_project_api.enums.TransmissionEnum;

public interface VehicleService {
  VehicleResponseDTO createVehicle(VehicleRequestDTO dto);

  VehicleResponseDTO getVehicleById(Long id);

  List<VehicleResponseDTO> getAllVehicles();

  // FIX: replaces getAllVehiclesByBrand (dead code — had no controller
  // endpoint). All params optional; pass null for whichever aren't filtered.
  List<VehicleResponseDTO> searchVehicles(
      Long brandId, CarTypeEnum type, TransmissionEnum transmission,
      FuelTypeEnum fuelType, BigDecimal minPrice, BigDecimal maxPrice, Integer seats);

  VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto);

  void deleteVehicle(Long id);
}