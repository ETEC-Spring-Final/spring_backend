package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.discount_usage.DiscountUsageRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount_usage.DiscountUsageResponseDTO;
import com.example.spring_boot_project_api.service.DiscountUsageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/discount-usages")
public class DiscountUsageController {
  @Autowired
  private DiscountUsageService discountUsageService;

  @PostMapping
  public DiscountUsageResponseDTO createDiscountUsage(@Valid @RequestBody DiscountUsageRequestDTO dto) {
    return discountUsageService.createDiscountUsage(dto);
  }

  @GetMapping("/{id}")
  public DiscountUsageResponseDTO getDiscountUsageById(@PathVariable Long id) {
    return discountUsageService.getDiscountUsageById(id);
  }

  @GetMapping("/my-discount-usages")
  public List<DiscountUsageResponseDTO> getMyDiscountUsages() {
    return discountUsageService.getMyDiscountUsages();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<DiscountUsageResponseDTO> getAllDiscountUsages(@PathVariable Long id) {
    return discountUsageService.getAllDiscountUsages();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public DiscountUsageResponseDTO updateDiscountUsage(@PathVariable Long id,
      @Valid @RequestBody DiscountUsageRequestDTO dto) {
    return discountUsageService.updateDiscountUsage(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteDiscountUsage(@PathVariable Long id) {
    discountUsageService.deleteDiscountUsage(id);
  }
}
