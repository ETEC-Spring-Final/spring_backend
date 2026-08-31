package com.example.spring_boot_project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  // Fetch all public reviews for a vehicle (Paginated)
  Page<Review> findByVehicleIdAndIsVisibleTrue(Long vehicleId, Pageable pageable);

  // Fetch all reviews left by a specific customer
  Page<Review> findByUserId(Long userId, Pageable pageable);

  // Prevent duplicate reviews: check if an invoice/rental was already reviewed by
  // the user
  boolean existsByRentalIdAndUserId(Long rentalId, Long userId);

  // Get average rating for a specific vehicle
  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicle.id = :vehicleId AND r.isVisible = true")
  Double findAverageRatingByVehicleId(@Param("vehicleId") Long vehicleId);

  // Get total review count for a vehicle
  Long countByVehicleIdAndIsVisibleTrue(Long vehicleId);

  // Filter count by specific rating score (1-5 stars)
  Long countByVehicleIdAndRatingAndIsVisibleTrue(Long vehicleId, Integer rating);
}