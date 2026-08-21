package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.discount.DiscountRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;
import com.example.spring_boot_project_api.model.Discount;
import com.example.spring_boot_project_api.repository.DiscountRepository;
import com.example.spring_boot_project_api.service.DiscountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
  private final DiscountRepository discountRepository;

  @Override
  public DiscountResponseDTO createDiscount(DiscountRequestDTO dto) {
    if (discountRepository.findByCode(dto.getCode()).isPresent()) {
      throw new RuntimeException("Discount code already exists");
    }

    Discount discount = new Discount();
    discount.setCode(dto.getCode());
    discount.setDescription(dto.getDescription());
    discount.setType(dto.getType());
    discount.setValue(dto.getValue());
    discount.setValidFrom(dto.getValidFrom());
    discount.setValidTo(dto.getValidTo());
    discount.setMaxUses(dto.getMaxUses());
    discount.setIsActive(dto.getIsActive());

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
  public DiscountResponseDTO updateDiscount(Long id, DiscountRequestDTO dto) {
    Discount discount = discountRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Discount not found"));

    discountRepository.findByCode(dto.getCode()).filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new RuntimeException("Discount code already exist");
        });

    discount.setCode(dto.getCode());
    discount.setDescription(dto.getDescription());
    discount.setType(dto.getType());
    discount.setValue(dto.getValue());
    discount.setValidFrom(dto.getValidFrom());
    discount.setValidTo(dto.getValidTo());
    discount.setMaxUses(dto.getMaxUses());
    discount.setIsActive(dto.getIsActive());

    Discount saved = discountRepository.save(discount);
    return toResponse(saved);
  }

  @Override
  public void deleteDiscount(Long id) {
    if (!discountRepository.existsById(id)) {
      throw new RuntimeException("Discount not found");
    }
    discountRepository.deleteById(id);
  }

  private DiscountResponseDTO toResponse(Discount d) {
    return new DiscountResponseDTO(d.getId(), d.getCode(), d.getDescription(), d.getType(), d.getValue(),
        d.getValidFrom(), d.getValidTo(), d.getMaxUses(), d.getUsedCount(), d.getIsActive());
  }
}
