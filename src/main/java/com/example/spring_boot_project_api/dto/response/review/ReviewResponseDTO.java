package com.example.spring_boot_project_api.dto.response.review;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReviewResponseDTO {
  private Long id;
  private Long rentalId;
  private Long vehicleId;
  private Long userId;
  private String userName;
  private Integer rating;
  private String comment;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
