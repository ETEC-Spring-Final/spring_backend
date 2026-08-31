package com.example.spring_boot_project_api.dto.response.rental_document;

import com.example.spring_boot_project_api.dto.response.attachment.AttachmentResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalDocumentResponseDTO {
  private Long id;
  private Long rentalId;
  private AttachmentResponseDTO attachment;
}
