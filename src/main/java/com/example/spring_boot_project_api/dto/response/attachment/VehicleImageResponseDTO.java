package com.example.spring_boot_project_api.dto.response.attachment;

import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.DocumentTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleImageResponseDTO {
  private Long vehicleImageId;
  private Long attachmentId;
  private Long vehicleId;
  private String fileUrl;
  private DocumentTypeEnum documentType;
  private Boolean isPrimary;
  private Integer displayOrder;
  private LocalDateTime uploadedAt;
}