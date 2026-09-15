package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.vehicle_image.VehicleImageRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle_image.VehicleImageResponseDTO;
import com.example.spring_boot_project_api.service.VehicleImageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicle-images")
public class VehicleImageController {
  @Autowired
  private VehicleImageService vehicleImageService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping
  public VehicleImageResponseDTO createVehicleImage(@Valid @RequestBody VehicleImageRequestDTO dto) {
    return vehicleImageService.createVehicleImage(dto);
  }

  // public — customers viewing the detail page need this
  @GetMapping("/{vehicleId}")
  public List<VehicleImageResponseDTO> getImagesByVehicleId(@PathVariable Long vehicleId) {
    return vehicleImageService.getImagesByVehicleId(vehicleId);
  }

  @GetMapping
  public List<VehicleImageResponseDTO> getAllVehicleImages() {
    return vehicleImageService.getAllVehicleImages();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}")
  public VehicleImageResponseDTO updateVehicleImage(@PathVariable Long id,
      @Valid @RequestBody VehicleImageRequestDTO dto) {
    return vehicleImageService.updateVehicleImage(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public void deleteVehicleImage(@PathVariable Long id) {
    vehicleImageService.deleteVehicleImage(id);
  }

  // DEPRECATED (2026-09): saved files to local disk via AttachmentServiceImpl.uploadAttachment(),
  // which Spring does not serve as a static resource → images 404 in browser (grey box bug).
  // Confirmed unused by any frontend (grep across vue_frontend found no callers) — safe to
  // delete entirely in a future cleanup. Replaced by the 2-step Cloudinary flow now used in
  // VehicleManagement.vue:
  //   1) POST /api/attachments     { fileUrl, documentType: "VEHICLE_IMAGE", isPrimary, displayOrder }
  //   2) POST /api/vehicle-images  { vehicleId, attachmentId }
  //
  // @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  // @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  // public VehicleImageResponseDTO upload(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
  //   return vehicleImageService.uploadImage(id, image);
  // }
}