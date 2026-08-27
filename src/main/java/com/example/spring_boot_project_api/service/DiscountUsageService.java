package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.discount_usage.DiscountUsageRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount_usage.DiscountUsageResponseDTO;

public interface DiscountUsageService {
  DiscountUsageResponseDTO createDiscountUsage(DiscountUsageRequestDTO dto);

  DiscountUsageResponseDTO getDiscountUsageById(Long id);

  List<DiscountUsageResponseDTO> getMyDiscountUsages();

  List<DiscountUsageResponseDTO> getAllDiscountUsages();

  DiscountUsageResponseDTO updateDiscountUsage(Long id, DiscountUsageRequestDTO dto);

  void deleteDiscountUsage(Long id);
}
