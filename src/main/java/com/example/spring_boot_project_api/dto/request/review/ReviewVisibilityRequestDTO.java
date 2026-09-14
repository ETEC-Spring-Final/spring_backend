package com.example.spring_boot_project_api.dto.request.review;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewVisibilityRequestDTO {
  @NotNull
  private Boolean isVisible;
}