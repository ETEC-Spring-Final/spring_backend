package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;

public interface DiscountService {
  DiscountResponseDTO createDiscount(DiscountRequestDTO dto);

  DiscountResponseDTO getDiscountById(Long id);

  List<DiscountResponseDTO> getAllDiscounts();

  DiscountResponseDTO updateDiscount(Long id, DiscountRequestDTO dto);

  void deleteDiscount(Long id);
}
