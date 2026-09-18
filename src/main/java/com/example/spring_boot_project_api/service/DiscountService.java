package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;

public interface DiscountService {
  DiscountResponseDTO createDiscount(DiscountRequestDTO dto);

  DiscountResponseDTO getDiscountById(Long id);

  List<DiscountResponseDTO> getAllDiscounts();

  /**
   * Promo codes a customer can actually redeem right now: isActive = true,
   * inside the valid-from / valid-to window, and not exhausted (usedCount
   * below maxUses). Backs GET /api/discounts/active.
   */
  List<DiscountResponseDTO> getActiveDiscounts();

  DiscountResponseDTO updateDiscount(Long id, DiscountRequestDTO dto);

  void deleteDiscount(Long id);
}