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

  /**
   * Applies a promo code to the given subtotal. Returns the discount amount
   * (0 when the code is null/blank). Throws when the code is unknown,
   * inactive, outside its validity window, or exhausted. Consumes one use
   * of the code.
   */
  java.math.BigDecimal applyDiscount(String code, java.math.BigDecimal subtotal);
}