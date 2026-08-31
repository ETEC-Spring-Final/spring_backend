package com.example.spring_boot_project_api.dto.response.vehicle_image;

import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleImageResponseDTO {
  private Long id;
  private Long vehicleId;
  private AttachmentResponseDTO attachment;
}
