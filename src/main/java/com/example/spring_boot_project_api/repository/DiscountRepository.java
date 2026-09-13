package com.example.spring_boot_project_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

  // FIX: this previously returned List<DiscountResponseDTO>, which is not
  // a valid projection for a plain derived query (no entity property
  // named after the DTO, and no @Query constructor expression). It also
  // wasn't used anywhere. Returning the entity itself is correct and safe.
  List<Discount> findByIsActiveTrue();

  // FIX: use case-insensitive lookup so "SUMMER10" and "summer10" are
  // treated as the same code (paired with normalizing the code to
  // uppercase on save in the service).
  Optional<Discount> findByCodeIgnoreCase(String code);
}