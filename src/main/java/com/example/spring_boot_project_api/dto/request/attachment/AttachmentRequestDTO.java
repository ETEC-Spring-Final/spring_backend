package com.example.spring_boot_project_api.dto.request.attachment;

import com.example.spring_boot_project_api.enums.DocumentTypeEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttachmentRequestDTO {
  @NotBlank
  private String fileUrl;

  private DocumentTypeEnum documentType;

  private Boolean isPrimary;

  private Integer displayOrder;
}
