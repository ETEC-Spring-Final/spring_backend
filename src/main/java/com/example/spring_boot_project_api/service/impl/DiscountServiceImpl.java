package com.example.spring_boot_project_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;
import com.example.spring_boot_project_api.enums.DiscountTypeEnum;
import com.example.spring_boot_project_api.model.Discount;
import com.example.spring_boot_project_api.repository.DiscountRepository;
import com.example.spring_boot_project_api.service.DiscountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
  private final DiscountRepository discountRepository;

  @Override
  @Transactional
  public DiscountResponseDTO createDiscount(DiscountRequestDTO dto) {
    validateBusinessRules(dto);

    String normalizedCode = dto.getCode().trim().toUpperCase();
    if (discountRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
      throw new RuntimeException("Discount code already exists");
    }

    Discount discount = new Discount();
    discount.setCode(normalizedCode);
    discount.setDescription(dto.getDescription());
    discount.setType(dto.getType());
    discount.setValue(dto.getValue());
    discount.setValidFrom(dto.getValidFrom());
    discount.setValidTo(dto.getValidTo());
    discount.setMaxUses(dto.getMaxUses());
    // FIX: default to true instead of persisting null when the caller
    // (e.g. a direct API call) omits isActive.
    discount.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

    Discount saved = discountRepository.save(discount);
    return toResponse(saved);
  }

  @Override
  public DiscountResponseDTO getDiscountById(Long id) {
    Discount discount = discountRepository.findById(id).orElseThrow(() -> new RuntimeException("Discount not found"));

    return toResponse(discount);
  }

  @Override
  public List<DiscountResponseDTO> getAllDiscounts() {
    return discountRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  /**
   * FIX (Phase A): backs the new GET /api/discounts/active. This finally puts
   * the previously-unused findByIsActiveTrue() query to work, then applies the
   * two rules a plain isActive flag cannot express — the validity window and
   * the usage cap — so an expired or exhausted code never reaches the booking
   * form as a selectable promo.
   */
  @Override
  public List<DiscountResponseDTO> getActiveDiscounts() {
    return discountRepository.findByIsActiveTrue().stream()
        .filter(this::isRedeemable)
        .map(this::toResponse)
        .toList();
  }

  @Override
  @Transactional
  public DiscountResponseDTO updateDiscount(Long id, DiscountRequestDTO dto) {
    validateBusinessRules(dto);

    Discount discount = discountRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Discount not found"));

    String normalizedCode = dto.getCode().trim().toUpperCase();
    discountRepository.findByCodeIgnoreCase(normalizedCode)
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new RuntimeException("Discount code already exists");
        });

    discount.setCode(normalizedCode);
    discount.setDescription(dto.getDescription());
    discount.setType(dto.getType());
    discount.setValue(dto.getValue());
    discount.setValidFrom(dto.getValidFrom());
    discount.setValidTo(dto.getValidTo());
    discount.setMaxUses(dto.getMaxUses());
    discount.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : discount.getIsActive());

    Discount saved = discountRepository.save(discount);
    return toResponse(saved);
  }

  @Override
  @Transactional
  public void deleteDiscount(Long id) {
    if (!discountRepository.existsById(id)) {
      throw new RuntimeException("Discount not found");
    }
    discountRepository.deleteById(id);
  }

  @Override
  @Transactional
  public BigDecimal applyDiscount(String code, BigDecimal subtotal) {
    if (code == null || code.isBlank()) {
      return BigDecimal.ZERO;
    }

    Discount discount = discountRepository.findByCodeIgnoreCase(code.trim())
        .orElseThrow(() -> new RuntimeException("Discount code not found"));

    if (discount.getIsActive() == null || !discount.getIsActive()) {
      throw new RuntimeException("Discount code is inactive");
    }
    if (!isRedeemable(discount)) {
      throw new RuntimeException("Discount code is expired or has reached its usage limit");
    }

    BigDecimal amount;
    if (discount.getType() == DiscountTypeEnum.PERCENTAGE) {
      amount = subtotal
          .multiply(discount.getValue())
          .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    } else {
      amount = discount.getValue().min(subtotal);
    }

    discount.setUsedCount((discount.getUsedCount() != null ? discount.getUsedCount() : 0) + 1);
    discountRepository.save(discount);

    return amount;
  }

  // ===== FIX: business rules that were previously missing entirely =====
  private void validateBusinessRules(DiscountRequestDTO dto) {
    // FIX: guard against a null value before comparing. If DiscountRequestDTO
    // ever loses its @NotNull (or a test builds the DTO by hand) the old code
    // threw a raw NullPointerException, which GlobalExceptionHandler turns
    // into an opaque 500 instead of a readable 400.
    if (dto.getValue() == null) {
      throw new RuntimeException("Value is required");
    }
    if (dto.getCode() == null || dto.getCode().isBlank()) {
      throw new RuntimeException("Code is required");
    }
    if (dto.getType() == null) {
      throw new RuntimeException("Discount type is required");
    }
    if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("Value must be greater than 0");
    }
    if (dto.getType() == DiscountTypeEnum.PERCENTAGE
        && dto.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
      throw new RuntimeException("Percentage discount value cannot exceed 100");
    }
    if (dto.getValidTo() != null && dto.getValidFrom() != null
        && dto.getValidTo().isBefore(dto.getValidFrom())) {
      throw new RuntimeException("Valid-to date must be after valid-from date");
    }
    if (dto.getMaxUses() != null && dto.getMaxUses() < 1) {
      throw new RuntimeException("Max uses must be at least 1");
    }
  }

  /** A code is redeemable when it is inside its window and not used up. */
  private boolean isRedeemable(Discount d) {
    if (isFutureDate(d.getValidFrom())) {
      return false; // has not started yet
    }
    if (isPastDate(d.getValidTo())) {
      return false; // already expired
    }
    if (d.getMaxUses() != null) {
      int used = d.getUsedCount() != null ? d.getUsedCount() : 0;
      if (used >= d.getMaxUses()) {
        return false; // usage cap reached
      }
    }
    return true;
  }

  // These two helpers take Object on purpose so they compile whether the
  // Discount entity declares validFrom/validTo as LocalDate or LocalDateTime.
  private boolean isFutureDate(Object value) {
    if (value instanceof LocalDate date) {
      return date.isAfter(LocalDate.now());
    }
    if (value instanceof LocalDateTime dateTime) {
      return dateTime.isAfter(LocalDateTime.now());
    }
    return false; // null → no lower bound
  }

  private boolean isPastDate(Object value) {
    if (value instanceof LocalDate date) {
      return date.isBefore(LocalDate.now());
    }
    if (value instanceof LocalDateTime dateTime) {
      return dateTime.isBefore(LocalDateTime.now());
    }
    return false; // null → never expires
  }

  private DiscountResponseDTO toResponse(Discount d) {
    return new DiscountResponseDTO(d.getId(), d.getCode(), d.getDescription(), d.getType(), d.getValue(),
        d.getValidFrom(), d.getValidTo(), d.getMaxUses(), d.getUsedCount(), d.getIsActive());
  }
}