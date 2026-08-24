package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.vehicle_image.VehicleImageRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle_image.VehicleImageResponseDTO;
import com.example.spring_boot_project_api.service.VehicleImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicle_images")
public class VehicleImageController {
  @Autowired
  private VehicleImageService vehicleImageService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public VehicleImageResponseDTO createVehicleImage(@Valid @RequestBody VehicleImageRequestDTO dto) {
    return vehicleImageService.createVehicleImage(dto);
  }

  @GetMapping("/{id}")
  public List<VehicleImageResponseDTO> getImagesByVehicleId(@PathVariable Long id) {
    return vehicleImageService.getImagesByVehicleId(id);
  }

  @GetMapping
  public List<VehicleImageResponseDTO> getAllVehicleImages() {
    return vehicleImageService.getAllVehicleImages();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public VehicleImageResponseDTO updateVehicleImage(@PathVariable Long id,
      @Valid @RequestBody VehicleImageRequestDTO dto) {
    return vehicleImageService.updateVehicleImage(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteVehicleImage(@PathVariable Long id) {
    vehicleImageService.deleteVehicleImage(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public VehicleImageResponseDTO upload(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
    return vehicleImageService.uploadImage(id, image);
  }
}
