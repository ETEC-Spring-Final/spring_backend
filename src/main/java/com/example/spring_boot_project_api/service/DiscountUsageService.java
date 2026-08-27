package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.discount_usage.DiscountUsageRequestDTO;
import com.example.spring_boot_project_api.model.DiscountUsage;

public interface DiscountUsageService {
  DiscountUsage createDiscountUsage(DiscountUsageRequestDTO dto);

  DiscountUsage getDiscountUsageById(Long id);

  List<DiscountUsage> getAllDiscountUsages();

  DiscountUsage updateDiscountUsage(Long id, DiscountUsageRequestDTO dto);

  void deleteDiscountUsage(Long id);
}
