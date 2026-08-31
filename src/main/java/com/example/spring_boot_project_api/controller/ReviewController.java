package com.example.spring_boot_project_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.review.ReviewRequestDTO;
import com.example.spring_boot_project_api.dto.response.review.ReviewResponseDTO;
import com.example.spring_boot_project_api.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
  @Autowired
  private ReviewService reviewService;

  @PostMapping
  public ReviewResponseDTO createReview(@Valid @RequestBody ReviewRequestDTO dto) {
    return reviewService.createReview(dto);
  }

  @PutMapping("/{id}")
  public ReviewResponseDTO updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequestDTO dto) {
    return reviewService.updateReview(id, dto);
  }

  @DeleteMapping("/{id}")
  public void deleteReview(@PathVariable Long id) {
    reviewService.deleteReview(id);
  }

  @GetMapping("/{id}")
  public ReviewResponseDTO getReviewById(@PathVariable Long id) {
    return reviewService.getReviewById(id);
  }

  @GetMapping("/vehicle/{vehicleId}")
  public Page<ReviewResponseDTO> getReviewsByVehicleId(@PathVariable Long vehicleId,
      @PageableDefault(size = 8, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable) {
    return reviewService.getReviewsByVehicleId(vehicleId, pageable);
  }

  @GetMapping("/vehicle/{vehicleId}/count")
  public Long countReviewsByRating(@PathVariable Long vehicleId, @RequestParam Integer rating) {
    return reviewService.countReviewsByRating(vehicleId, rating);
  }

  @GetMapping("/my-reviews")
  public Page<ReviewResponseDTO> getMyReviews(Pageable pageable) {
    return reviewService.getMyReviews(pageable);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public Page<ReviewResponseDTO> getAllReviews(
      @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    return reviewService.getAllReviews(pageable);
  }
}
