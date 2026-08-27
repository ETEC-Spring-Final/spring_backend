package com.example.spring_boot_project_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.dto.response.discount.DiscountResponseDTO;
import com.example.spring_boot_project_api.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
  List<DiscountResponseDTO> findByIsActiveTrue();

  Optional<Discount> findByCode(String code);
}
