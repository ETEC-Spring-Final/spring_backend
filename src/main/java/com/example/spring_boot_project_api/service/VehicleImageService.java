package com.example.spring_boot_project_api.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.vehicle_image.VehicleImageRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle_image.VehicleImageResponseDTO;

public interface VehicleImageService {
  VehicleImageResponseDTO createVehicleImage(VehicleImageRequestDTO dto);

  VehicleImageResponseDTO uploadImage(Long vehicleId, MultipartFile file);

  List<VehicleImageResponseDTO> getAllVehicleImages();

  List<VehicleImageResponseDTO> getImagesByVehicleId(Long vehicleId);

  VehicleImageResponseDTO updateVehicleImage(Long id, VehicleImageRequestDTO dto);

  void deleteVehicleImage(Long id);
}
