package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.discount_usage.DiscountUsageRequestDTO;
import com.example.spring_boot_project_api.dto.response.discount_usage.DiscountUsageResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.Discount;
import com.example.spring_boot_project_api.model.DiscountUsage;
import com.example.spring_boot_project_api.model.Reservation;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.DiscountRepository;
import com.example.spring_boot_project_api.repository.DiscountUsageRepository;
import com.example.spring_boot_project_api.repository.ReservationRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.DiscountUsageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountUsageServiceImpl implements DiscountUsageService {
  private final DiscountUsageRepository discountUsageRepository;
  private final UserRepository userRepository;
  private final DiscountRepository discountRepository;
  private final ReservationRepository reservationRepository;

  @Override
  public DiscountUsageResponseDTO createDiscountUsage(DiscountUsageRequestDTO dto) {
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    Discount discount = discountRepository.findById(dto.getDiscountId())
        .orElseThrow(() -> new RuntimeException("Discount not found"));

    User currentUser = getCurrentUser();

    if (!reservation.getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("This reservation does not belong to you");
    }

    if (discountUsageRepository.existsByDiscountIdAndUserId(dto.getDiscountId(), currentUser.getId())) {
      throw new RuntimeException("You have already used this discount code");
    }

    if (discount.getMaxUses() != null && discount.getUsedCount() >= discount.getMaxUses()) {
      throw new RuntimeException("This discount code has reached its usage limit");
    }

    DiscountUsage discountUsage = new DiscountUsage();
    discountUsage.setDiscount(discount);
    discountUsage.setUser(currentUser);
    discountUsage.setReservation(reservation);

    DiscountUsage saved = discountUsageRepository.save(discountUsage);

    discount.setUsedCount(discount.getUsedCount() + 1);
    discountRepository.save(discount);

    return toResponse(saved);
  }

  @Override
  public DiscountUsageResponseDTO getDiscountUsageById(Long id) {
    DiscountUsage discountUsage = discountUsageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Discount Usage not found"));

    User currentUser = getCurrentUser();
    boolean isOwner = discountUsage.getUser().getId().equals(currentUser.getId());
    boolean isStaff = currentUser.getRole() != RoleEnum.CUSTOMER;

    if (!isOwner && !isStaff) {
      throw new RuntimeException("You are not authorized to view this discount usage");
    }

    return toResponse(discountUsage);
  }

  @Override
  public List<DiscountUsageResponseDTO> getMyDiscountUsages() {
    User currentUser = getCurrentUser();
    return discountUsageRepository.findByUserId(currentUser.getId()).stream().map(this::toResponse).toList();
  }

  @Override
  public List<DiscountUsageResponseDTO> getAllDiscountUsages() {
    return discountUsageRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Override
  public DiscountUsageResponseDTO updateDiscountUsage(Long id, DiscountUsageRequestDTO dto) {
    User currentUser = getCurrentUser();

    DiscountUsage discountUsage = discountUsageRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Discount usage not found"));

    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    Discount discount = discountRepository.findById(dto.getDiscountId())
        .orElseThrow(() -> new RuntimeException("Discount not found"));

    if (!reservation.getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("This reservation does not belong to you");
    }

    discountUsage.setDiscount(discount);
    discountUsage.setReservation(reservation);

    DiscountUsage saved = discountUsageRepository.save(discountUsage);
    return toResponse(saved);
  }

  @Override
  public void deleteDiscountUsage(Long id) {
    if (!discountUsageRepository.existsById(id)) {
      throw new RuntimeException("Discount Usage not found");
    }
    discountUsageRepository.deleteById(id);
  }

  private DiscountUsageResponseDTO toResponse(DiscountUsage du) {
    return new DiscountUsageResponseDTO(du.getId(), du.getDiscount().getId(), du.getUser().getId(),
        du.getReservation().getId(), du.getUsedAt());
  }

  // Ownership function
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    return userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
  }
}
