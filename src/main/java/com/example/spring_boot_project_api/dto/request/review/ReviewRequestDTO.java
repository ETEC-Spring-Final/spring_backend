package com.example.spring_boot_project_api.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
  @NotNull
  private Long rentalId;

  @Max(5)
  private Integer rating;

  private String comment;
}
