package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.spring_boot_project_api.dto.request.vehicle_image.VehicleImageRequestDTO;
import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;
import com.example.spring_boot_project_api.dto.response.vehicle_image.VehicleImageResponseDTO;
import com.example.spring_boot_project_api.model.Attachment;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.model.VehicleImage;
import com.example.spring_boot_project_api.repository.AttachmentRepository;
import com.example.spring_boot_project_api.repository.VehicleImageRepository;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.AttachmentService;
import com.example.spring_boot_project_api.service.VehicleImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleImageServiceImpl implements VehicleImageService {
  private final VehicleImageRepository vehicleImageRepository;
  private final VehicleRepository vehicleRepository;
  private final AttachmentRepository attachmentRepository;
  private final AttachmentService attachmentService;

  @Override
  public VehicleImageResponseDTO createVehicleImage(VehicleImageRequestDTO dto) {
    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    Attachment attachment = attachmentRepository.findById(dto.getAttachmentId())
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    VehicleImage vehicleImage = new VehicleImage();
    vehicleImage.setVehicle(vehicle);
    vehicleImage.setAttachment(attachment);

    VehicleImage saved = vehicleImageRepository.save(vehicleImage);
    return toResponse(saved);
  }

  // ============UPLOAD IMAGE============
  @Override
  public VehicleImageResponseDTO uploadImage(Long vehicleId, MultipartFile file) {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    AttachmentResponseDTO attachmentDto = attachmentService.uploadAttachment(file, null);
    Attachment attachment = attachmentRepository.findById(attachmentDto.getId())
        .orElseThrow(() -> new RuntimeException("Attachment not found"));

    VehicleImage vehicleImage = new VehicleImage();
    vehicleImage.setVehicle(vehicle);
    vehicleImage.setAttachment(attachment);

    VehicleImage saved = vehicleImageRepository.save(vehicleImage);
    return toResponse(saved);
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
    VehicleImage vehicleImage = vehicleImageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Image not found"));

    Attachment attachment = vehicleImage.getAttachment();
    attachment.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : attachment.getIsPrimary());
    attachment.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : attachment.getDisplayOrder());
    attachmentRepository.save(attachment);

    return toResponse(vehicleImage);
  }

  @Override
  public void deleteVehicleImage(Long id) {
    if (!vehicleImageRepository.existsById(id)) {
      throw new RuntimeException("Image not found");
    }
    vehicleImageRepository.deleteById(id);
  }

  private VehicleImageResponseDTO toResponse(VehicleImage vi) {
    AttachmentResponseDTO attachmentDto = new AttachmentResponseDTO(
        vi.getAttachment().getId(), vi.getAttachment().getFileUrl(), vi.getAttachment().getDocumentType(),
        vi.getAttachment().getIsPrimary(), vi.getAttachment().getDisplayOrder(), vi.getAttachment().getUploadedAt());

    return new VehicleImageResponseDTO(vi.getId(), vi.getVehicle().getId(), attachmentDto);
  }
}
