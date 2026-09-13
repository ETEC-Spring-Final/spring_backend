package com.example.spring_boot_project_api.service.impl;

import java.math.BigDecimal;
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

  // ===== FIX: business rules that were previously missing entirely =====
  private void validateBusinessRules(DiscountRequestDTO dto) {
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

  private DiscountResponseDTO toResponse(Discount d) {
    return new DiscountResponseDTO(d.getId(), d.getCode(), d.getDescription(), d.getType(), d.getValue(),
        d.getValidFrom(), d.getValidTo(), d.getMaxUses(), d.getUsedCount(), d.getIsActive());
  }
}