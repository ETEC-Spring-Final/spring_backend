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

  // DEPRECATED: saves to local disk (grey-box bug). Unused by any frontend as of 2026-09
  // (confirmed via grep). Kept only so this class still implements the interface method —
  // the controller route that called this is commented out. Do not call from new code.
  @Deprecated
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

    List<VehicleImage> existing = vehicleImageRepository.findByVehicleId(vehicleId);
    boolean isFirstImage = existing.size() == 1;
    attachment.setIsPrimary(isFirstImage);
    attachment.setDisplayOrder(existing.size() - 1);
    attachmentRepository.save(attachment);

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

    if (Boolean.TRUE.equals(dto.getIsPrimary())) {
      List<VehicleImage> siblings = vehicleImageRepository.findByVehicleId(vehicleImage.getVehicle().getId());
      for (VehicleImage sibling : siblings) {
        if (!sibling.getId().equals(id) && Boolean.TRUE.equals(sibling.getAttachment().getIsPrimary())) {
          sibling.getAttachment().setIsPrimary(false);
          attachmentRepository.save(sibling.getAttachment());
        }
      }
    }

    attachment.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : attachment.getIsPrimary());
    attachment.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : attachment.getDisplayOrder());
    attachmentRepository.save(attachment);

    return toResponse(vehicleImage);
  }

  @Override
  public void deleteVehicleImage(Long id) {
    VehicleImage vehicleImage = vehicleImageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Image not found"));

    Long attachmentId = vehicleImage.getAttachment().getId();
    boolean wasPrimary = Boolean.TRUE.equals(vehicleImage.getAttachment().getIsPrimary());
    Long vehicleId = vehicleImage.getVehicle().getId();

    vehicleImageRepository.deleteById(id);
    attachmentRepository.deleteById(attachmentId);

    if (wasPrimary) {
      vehicleImageRepository.findByVehicleId(vehicleId).stream().findFirst().ifPresent(next -> {
        next.getAttachment().setIsPrimary(true);
        attachmentRepository.save(next.getAttachment());
      });
    }
  }

  private VehicleImageResponseDTO toResponse(VehicleImage vi) {
    AttachmentResponseDTO attachmentDto = new AttachmentResponseDTO(
        vi.getAttachment().getId(), vi.getAttachment().getFileUrl(), vi.getAttachment().getDocumentType(),
        vi.getAttachment().getIsPrimary(), vi.getAttachment().getDisplayOrder(), vi.getAttachment().getUploadedAt());

    return new VehicleImageResponseDTO(vi.getId(), vi.getVehicle().getId(), attachmentDto);
  }
}