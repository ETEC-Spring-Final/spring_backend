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

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;
import com.example.spring_boot_project_api.service.DiscountService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

  private final DiscountService discountService;

  // NOTE: only ADMIN/MANAGER can create, update or delete discounts.
  // Previously there was no @PreAuthorize here at all, so ANY visitor
  // (even without logging in) could hit these endpoints directly, since
  // SecurityConfig permits all requests at the URL level and relies on
  // method security for endpoints like this one.

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping
  public DiscountResponseDTO createDiscount(@Valid @RequestBody DiscountRequestDTO dto) {
    return discountService.createDiscount(dto);
  }

  /**
   * FIX (Phase A): the customer booking form and DiscountManagement.vue both
   * call GET /api/discounts/active, which did not exist — the frontend got a
   * 404 (or worse, fell through to /{id} and failed to parse "active" as a
   * Long). This returns only currently-redeemable promo codes and is readable
   * by any signed-in user, so a CUSTOMER can pick a promo on the reservation
   * form without being handed the full admin list.
   *
   * IMPORTANT: this mapping must be declared so Spring matches the literal
   * path before the /{id} path-variable mapping below.
   */
  @GetMapping("/active")
  public List<DiscountResponseDTO> getActiveDiscounts() {
    return discountService.getActiveDiscounts();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping("/{id}")
  public DiscountResponseDTO getDiscountById(@PathVariable Long id) {
    return discountService.getDiscountById(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public List<DiscountResponseDTO> getAllDiscounts() {
    return discountService.getAllDiscounts();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}")
  public DiscountResponseDTO updateDiscount(@PathVariable Long id, @Valid @RequestBody DiscountRequestDTO dto) {
    return discountService.updateDiscount(id, dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public void deleteDiscount(@PathVariable Long id) {
    discountService.deleteDiscount(id);
  }
}