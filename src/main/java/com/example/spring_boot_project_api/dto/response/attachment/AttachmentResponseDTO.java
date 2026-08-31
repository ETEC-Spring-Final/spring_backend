package com.example.spring_boot_project_api.dto.response.attachment;

import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.DocumentTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentResponseDTO {
  private Long id;
  private String fileUrl;
  private DocumentTypeEnum documentType;
  private Boolean isPrimary;
  private Integer displayOrder;
  private LocalDateTime uploadedAt;
}
