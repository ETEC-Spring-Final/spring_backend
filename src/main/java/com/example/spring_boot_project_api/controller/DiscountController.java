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

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;
import com.example.spring_boot_project_api.service.DiscountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
  @Autowired
  private DiscountService discountService;

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