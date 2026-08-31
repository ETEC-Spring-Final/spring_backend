package com.example.spring_boot_project_api.dto.request.rental_document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalDocumentRequestDTO {
  @NotNull
  private Long rentalId;

  @NotNull
  private Long attachmentId;
}
