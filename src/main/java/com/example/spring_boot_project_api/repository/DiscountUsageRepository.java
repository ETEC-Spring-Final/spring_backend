package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.DiscountUsage;

public interface DiscountUsageRepository extends JpaRepository<DiscountUsage, Long> {
  List<DiscountUsage> findByUserId(Long userId);

  List<DiscountUsage> findByReservationId(Long reservationId);

  List<DiscountUsage> findByDiscountId(Long discountId);

  boolean existsByDiscountIdAndUserId(Long discountId, Long userId);

  long countByDiscountId(Long discountId);
}
