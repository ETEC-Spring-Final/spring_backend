package com.example.spring_boot_project_api.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.example.spring_boot_project_api.config.UploadConfig.ALLOWED_EXTENSIONS;
import static com.example.spring_boot_project_api.config.UploadConfig.UPLOAD_DIR;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.vehicle_image.VehicleImageRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle_image.VehicleImageResponseDTO;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.model.VehicleImage;
import com.example.spring_boot_project_api.repository.VehicleImageRepository;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.VehicleImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleImageServiceImpl implements VehicleImageService {
  private final VehicleImageRepository vehicleImageRepository;

  private final VehicleRepository vehicleRepository;

  @Override
  public VehicleImageResponseDTO createVehicleImage(VehicleImageRequestDTO dto) {
    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    VehicleImage vehicleImage = new VehicleImage();
    vehicleImage.setVehicle(vehicle);
    vehicleImage.setImageUrl(dto.getImageUrl());
    vehicleImage.setIsPrimary(dto.getIsPrimary());
    vehicleImage.setDisplayOrder(dto.getDisplayOrder());

    VehicleImage saved = vehicleImageRepository.save(vehicleImage);
    return toResponse(saved);
  }

  // ============UPLOAD IMAGE============
  @Override
  public VehicleImageResponseDTO uploadImage(Long vehicleId, MultipartFile file) {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    if (file.isEmpty()) {
      throw new RuntimeException("No file selected");
    }

    String original = file.getOriginalFilename();
    String ext = original != null && original.contains(".")
        ? original.substring(original.lastIndexOf('.') + 1).toLowerCase()
        : "";
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
      throw new RuntimeException("Only jpg, jpeg, png, webp are allowed");
    }

    try {
      Path dir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
      Files.createDirectories(dir);
      String filename = "vehicle-" + vehicleId + "-" + System.currentTimeMillis() + "." + ext;
      file.transferTo(dir.resolve(filename).toFile());

      VehicleImage vehicleImage = new VehicleImage();
      vehicleImage.setVehicle(vehicle);
      vehicleImage.setImageUrl("/uploads/" + filename);

      VehicleImage saved = vehicleImageRepository.save(vehicleImage);
      return toResponse(saved);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file: " + e.getMessage());
    }
  }

  @Override
  public List<VehicleImageResponseDTO> getAllVehicleImages() {
    return vehicleImageRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<VehicleImageResponseDTO> getImagesByVehicleId(Long vehicleId) {
    return vehicleImageRepository.findByVehicleId(vehicleId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public VehicleImageResponseDTO updateVehicleImage(Long id, VehicleImageRequestDTO dto) {
    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    VehicleImage vehicleImage = vehicleImageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Image not found"));

    vehicleImage.setVehicle(vehicle);
    vehicleImage.setImageUrl(dto.getImageUrl());
    vehicleImage.setIsPrimary(dto.getIsPrimary());
    vehicleImage.setDisplayOrder(dto.getDisplayOrder());

    VehicleImage saved = vehicleImageRepository.save(vehicleImage);
    return toResponse(saved);
  }

  @Override
  public void deleteVehicleImage(Long id) {
    if (!vehicleImageRepository.existsById(id)) {
      throw new RuntimeException("Image not found");
    }
    vehicleImageRepository.deleteById(id);
  }

  private VehicleImageResponseDTO toResponse(VehicleImage vi) {
    return new VehicleImageResponseDTO(vi.getId(), vi.getVehicle().getId(), vi.getImageUrl(), vi.getIsPrimary(),
        vi.getDisplayOrder(), vi.getCreatedAt());
  }
}
