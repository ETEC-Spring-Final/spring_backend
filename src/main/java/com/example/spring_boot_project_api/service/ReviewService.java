package com.example.spring_boot_project_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.spring_boot_project_api.dto.request.review.ReviewRequestDTO;
import com.example.spring_boot_project_api.dto.response.review.ReviewResponseDTO;

public interface ReviewService {

  // Create & Update
  ReviewResponseDTO createReview(ReviewRequestDTO dto);

  ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto);

  void deleteReview(Long id);

  // Single Fetch
  ReviewResponseDTO getReviewById(Long id);

  // Paginated Reads (For high performance)
  Page<ReviewResponseDTO> getAllReviews(Pageable pageable);

  Page<ReviewResponseDTO> getReviewsByVehicleId(Long vehicleId, Pageable pageable);

  Page<ReviewResponseDTO> getMyReviews(Pageable pageable);

  Long countReviewsByRating(Long vehicleId, Integer rating);
}