package com.example.spring_boot_project_api.controller;

import java.util.List;

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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/discount-usages")
@RequiredArgsConstructor
public class DiscountUsageController {

  private final DiscountUsageService discountUsageService;

  @PostMapping
  public DiscountUsageResponseDTO createDiscountUsage(@Valid @RequestBody DiscountUsageRequestDTO dto) {
    return discountUsageService.createDiscountUsage(dto);
  }

  /**
   * Declared before /{id} so the literal path wins the match — otherwise
   * "my-discount-usages" is handed to the Long converter and blows up.
   */
  @GetMapping("/my-discount-usages")
  public List<DiscountUsageResponseDTO> getMyDiscountUsages() {
    return discountUsageService.getMyDiscountUsages();
  }

  @GetMapping("/{id}")
  public DiscountUsageResponseDTO getDiscountUsageById(@PathVariable Long id) {
    return discountUsageService.getDiscountUsageById(id);
  }

  /**
   * FIX (Phase A): this method declared `@PathVariable Long id` while its
   * @GetMapping has no {id} placeholder. Spring cannot resolve the variable, so
   * every call to GET /api/discount-usages failed at runtime instead of
   * returning the list. The parameter was unused anyway — the service takes no
   * argument — so it is simply removed.
   */
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<DiscountUsageResponseDTO> getAllDiscountUsages() {
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